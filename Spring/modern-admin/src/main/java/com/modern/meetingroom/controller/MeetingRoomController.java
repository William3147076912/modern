package com.modern.meetingroom.controller;

import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import com.modern.common.constant.HttpStatus;
import com.modern.common.core.page.PageDomain;
import com.modern.common.core.page.TableSupport;
import com.modern.order.domain.vo.OrdersVo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.modern.common.annotation.Log;
import com.modern.common.core.controller.BaseController;
import com.modern.common.core.domain.AjaxResult;
import com.modern.common.enums.BusinessType;
import com.modern.meetingroom.domain.MeetingRoom;
import com.modern.meetingroom.service.IMeetingRoomService;
import com.modern.common.utils.poi.ExcelUtil;
import com.modern.common.core.page.TableDataInfo;

/**
 * 会议室基础信息，记录公司所有会议室的配置及状态Controller
 *
 * @author cyk
 * @date 2025-04-03
 */
@RestController
@RequestMapping("/meetingroom/room")
public class MeetingRoomController extends BaseController {
    @Autowired
    private IMeetingRoomService meetingRoomService;

    /**
     * 查询会议室基础信息，记录公司所有会议室的配置及状态列表
     */
    @PreAuthorize("@ss.hasPermi('room:room:list')")
    @GetMapping("/list")
    public TableDataInfo list(MeetingRoom meetingRoom) {
        startPage();
        List<MeetingRoom> list = meetingRoomService.selectMeetingRoomList(meetingRoom);
        return getDataTable(list);
    }

    /**
     * 预定会议室
     */
    @PreAuthorize("@ss.hasPermi('room:room:reserve')")
    @GetMapping("/idleRoomList")
    public TableDataInfo idleRoomList(MeetingRoom meetingRoom, @RequestParam("usageTime") String usageTime) {
        startPage();
        List<MeetingRoom> list = meetingRoomService.selectIdleRoomList(meetingRoom, usageTime);
        return getDataTable(list);
    }

    /**
     * 导出会议室基础信息，记录公司所有会议室的配置及状态列表
     */
    @PreAuthorize("@ss.hasPermi('room:room:export')")
    @Log(title = "会议室基础信息，记录公司所有会议室的配置及状态", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MeetingRoom meetingRoom) {
        List<MeetingRoom> list = meetingRoomService.selectMeetingRoomList(meetingRoom);
        ExcelUtil<MeetingRoom> util = new ExcelUtil<MeetingRoom>(MeetingRoom.class);
        util.exportExcel(response, list, "会议室基础信息，记录公司所有会议室的配置及状态数据");
    }

    /**
     * 获取会议室基础信息，记录公司所有会议室的配置及状态详细信息
     */
    @PreAuthorize("@ss.hasPermi('room:room:query')")
    @GetMapping(value = "/{roomId}")
    public AjaxResult getInfo(@PathVariable("roomId") Long roomId) {
        return success(meetingRoomService.selectMeetingRoomByRoomId(roomId));
    }

    /**
     * 新增会议室基础信息，记录公司所有会议室的配置及状态
     */
    @PreAuthorize("@ss.hasPermi('room:room:add')")
    @Log(title = "会议室基础信息，记录公司所有会议室的配置及状态", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MeetingRoom meetingRoom) {
        return toAjax(meetingRoomService.insertMeetingRoom(meetingRoom));
    }

    /**
     * 修改会议室基础信息，记录公司所有会议室的配置及状态
     */
    @PreAuthorize("@ss.hasAnyPermi('room:room:edit,room:room:reserve')")
    @Log(title = "会议室基础信息，记录公司所有会议室的配置及状态", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MeetingRoom meetingRoom) {
        return toAjax(meetingRoomService.updateMeetingRoom(meetingRoom));
    }

    /**
     * 删除会议室基础信息，记录公司所有会议室的配置及状态
     */
    @PreAuthorize("@ss.hasPermi('room:room:remove')")
    @Log(title = "会议室基础信息，记录公司所有会议室的配置及状态", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roomIds}")
    public AjaxResult remove(@PathVariable Long[] roomIds) {
        return toAjax(meetingRoomService.deleteMeetingRoomByRoomIds(roomIds));
    }
}
