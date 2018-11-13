package com.qidian.hcm.module.authorization.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.qidian.hcm.common.enums.YesNo;
import com.qidian.hcm.common.exception.BizException;
import com.qidian.hcm.common.utils.ResultCode;
import com.qidian.hcm.module.authorization.dto.*;
import com.qidian.hcm.module.authorization.entity.Menu;
import com.qidian.hcm.module.authorization.entity.Role;
import com.qidian.hcm.module.authorization.entity.RoleMenu;
import com.qidian.hcm.module.authorization.entity.RolePermission;
import com.qidian.hcm.module.authorization.enums.MenuCode;
import com.qidian.hcm.module.authorization.enums.PlatformType;
import com.qidian.hcm.module.authorization.repository.MenuRepository;
import com.qidian.hcm.module.authorization.repository.RoleMenuRepository;
import com.qidian.hcm.module.authorization.repository.RolePermissionRepository;
import com.qidian.hcm.module.authorization.repository.RoleRepository;
import com.qidian.hcm.module.authorization.request.RoleEditRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色Service
 */
@Slf4j
@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private RoleMenuRepository roleMenuRepository;

    /**
     * 查找所有的角色信息
     *
     * @return
     */
    public List<RoleDTO> findAllRoleList() {
        List<Role> roleList = roleRepository.findAll();
        return roleList.stream().filter(e -> e.getSuperAdmin() == YesNo.NO)
                .map(e -> new RoleDTO(e.getId(), e.getName())).collect(Collectors.toList());
    }

    /**
     * 添加角色
     *
     * @param roleEditDTO
     */
    public Long addRole(RoleEditRequest roleEditDTO) {
        Role role = roleRepository.findByName(roleEditDTO.getName());
        if (Objects.isNull(role)) {
            role = new Role(roleEditDTO.getName());
            Role save = roleRepository.save(role);
            return save.getId();
        } else {
            throw new BizException(ResultCode.ROLE_NAME_EXISTS);
        }
    }

    /**
     * 修改角色
     *
     * @param roleEditDTO
     * @param roleId
     */
    public void updateRole(RoleEditRequest roleEditDTO, Long roleId) {
        Role r = roleRepository.findByNameAndIdNot(roleEditDTO.getName(), roleId);
        if (r != null) {
            throw new BizException(ResultCode.ROLE_NAME_EXISTS);
        }
        Optional<Role> roleOptional = roleRepository.findById(roleId);
        if (roleOptional.isPresent()) {
            Role role = roleOptional.get();
            role.setName(roleEditDTO.getName());
            roleRepository.save(role);
        } else {
            throw new BizException(ResultCode.ROLE_NOT_EXISTS);
        }
    }

    /**
     * 删除角色
     *
     * @param roleId
     */
    @Transactional
    public void deleteRole(Long roleId) {
        Optional<Role> roleOptional = roleRepository.findById(roleId);
        if (roleOptional.isPresent()) {
            // 删除角色同时会级联删除role_menu,role_permission
            roleRepository.delete(roleOptional.get());
        } else {
            throw new BizException(ResultCode.ROLE_NOT_EXISTS);
        }
    }

    /**
     * 保存角色权限信息
     *
     * @param rolePermissionDTO
     * @param roleId
     */
    @Transactional
    public void saveRolePermissionsList(RolePermissionDTO rolePermissionDTO, Long roleId) {
        Optional<Role> roleOptional = roleRepository.findById(roleId);
        if (roleOptional.isPresent()) {
            //删除该角色的菜单信息及权限信息
            roleMenuRepository.deleteAllByRoleId(roleId);
            rolePermissionRepository.deleteAllByRoleId(roleId);

            //处理角色的菜单和权限信息
            handlerMenuAndPermission(rolePermissionDTO, roleId);
        } else {
            throw new BizException(ResultCode.ROLE_NOT_EXISTS);
        }
    }

    /**
     * 菜单校验
     *
     * @param menuCode
     */
    private Menu validateMenuCode(MenuCode menuCode) {
        Menu menu = menuRepository.findByCode(menuCode);
        if (Objects.isNull(menu)) {
            throw new BizException(ResultCode.MENU_NOT_EXISTS);
        }
        return menu;
    }

    /**
     * 处理角色的菜单及权限信息
     *
     * @param rolePermissionDTO
     * @param roleId
     */
    private void handlerMenuAndPermission(RolePermissionDTO rolePermissionDTO, Long roleId) {
        List<PermissionMetaDataDTO> menuAndPermissionList = rolePermissionDTO.getBackend();
        if (menuAndPermissionList != null) {
            menuAndPermissionList.addAll(rolePermissionDTO.getFrontend());
        }
        saveMenuAndPermission(roleId, menuAndPermissionList);
    }

    /**
     * 角色菜单、角色权限数据入库
     *
     * @param roleId
     * @param list
     */
    private void saveMenuAndPermission(Long roleId, List<PermissionMetaDataDTO> list) {
        List<RoleMenu> menuList = new ArrayList<>();
        List<RolePermission> rolePermissionList = new ArrayList<>();
        if (list != null) {
            list.forEach(e -> {
                Menu menu = validateMenuCode(e.getCode());
                menuList.add(new RoleMenu(roleId, menu.getId()));
                List<PermissionDTO> permissions = e.getPermissions();
                if (permissions != null) {
                    permissions.forEach(permissionDTO -> {
                        RolePermission rolePermission = new RolePermission(roleId, menu.getCode(), "",
                                permissionDTO.getAction(), JSONObject.toJSONString(permissionDTO.getConditions()));
                        rolePermissionList.add(rolePermission);
                    });
                }
            });
            roleMenuRepository.saveAll(menuList);
            rolePermissionRepository.saveAll(rolePermissionList);
        }
    }

    /**
     * 获取角色权限配置
     *
     * @param roleId
     * @return
     */
    public RolePermissionDTO getRolePermissionsList(Long roleId) {
        validateRoleId(roleId);
        List<RolePermission> rolePermissionList = rolePermissionRepository.findAllByRoleId(roleId);
        Map<MenuCode, Menu> menuCodeEntityMap = menuRepository.findAll().stream()
                .collect(Collectors.toMap(Menu::getCode, menu -> menu));
        return convertToRolePermissionDTO(rolePermissionList, menuCodeEntityMap);
    }

    public Map<Long, RolePermissionDTO> getRolePermissionDTOMap(Collection<Long> roleIds) {
        List<RolePermission> rolePermissionList = rolePermissionRepository.findAllByRoleIdIn(roleIds);
        Map<Long, List<RolePermission>> roleIdEntityMap = rolePermissionList.stream()
                .collect(Collectors.groupingBy(RolePermission::getRoleId));

        Map<MenuCode, Menu> menuCodeEntityMap = menuRepository.findAll().stream()
                .collect(Collectors.toMap(Menu::getCode, menu -> menu));

        Set<Map.Entry<Long, List<RolePermission>>> roleIdEntrySet = roleIdEntityMap.entrySet();
        Map<Long, RolePermissionDTO> map = Maps.newHashMapWithExpectedSize(roleIdEntrySet.size());
        for (Map.Entry<Long, List<RolePermission>> entry : roleIdEntrySet) {
            RolePermissionDTO rolePermissionDTO = convertToRolePermissionDTO(entry.getValue(), menuCodeEntityMap);
            map.put(entry.getKey(), rolePermissionDTO);
        }
        return map;
    }

    private RolePermissionDTO convertToRolePermissionDTO(List<RolePermission> rolePermissionList,
                                                         Map<MenuCode, Menu> menuCodeEntityMap) {
        List<PermissionMetaDataDTO> mergedByMenuList = new ArrayList<>();

        rolePermissionList.forEach(e -> {
            PermissionDTO permissionDTO = new PermissionDTO(e.getAction(),
                    JSONObject.parseArray(e.getConditions(), ConditionDTO.class));

            Optional<PermissionMetaDataDTO> existMenuPermission = mergedByMenuList.stream()
                    .filter(m -> m.getCode() == e.getMenuCode()).findAny();
            if (existMenuPermission.isPresent()) {
                existMenuPermission.get().getPermissions().add(permissionDTO);
            } else {
                Menu menu = menuCodeEntityMap.get(e.getMenuCode());
                PermissionMetaDataDTO dto = new PermissionMetaDataDTO(menu.getName(), menu.getCode(),
                        menu.getType(), menu.getPlatform(), Lists.newArrayList(permissionDTO));
                mergedByMenuList.add(dto);
            }
        });

        Map<PlatformType, List<PermissionMetaDataDTO>> platformPermissionMap = mergedByMenuList.stream().collect(
                Collectors.groupingBy(PermissionMetaDataDTO::getPlatformType));

        List<PermissionMetaDataDTO> frontend = platformPermissionMap.get(PlatformType.frontend) == null ?
                Lists.newArrayList() : platformPermissionMap.get(PlatformType.frontend);
        List<PermissionMetaDataDTO> backend = platformPermissionMap.get(PlatformType.backend) == null ?
                Lists.newArrayList() : platformPermissionMap.get(PlatformType.backend);
        return new RolePermissionDTO(frontend, backend);
    }

    /**
     * roleId校验
     *
     * @param roleId
     */
    public void validateRoleId(Long roleId) {
        Optional<Role> roleOptional = roleRepository.findById(roleId);
        if (!roleOptional.isPresent()) {
            throw new BizException(ResultCode.ROLE_NOT_EXISTS);
        }
    }
}