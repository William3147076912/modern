<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="会议室名称" prop="roomName" label-width="100px">
        <el-input
            v-model="queryParams.roomName"
            placeholder="请输入会议室名称"
            clearable
            @keyup.enter="handleQuery"
            style="width: 200px"
        />
      </el-form-item>
      <!--<el-form-item label="选择日期" prop="selectedDate" v-hasPermi="['room:room:reserve']" label-width="100px">-->
      <!--  <el-date-picker-->
      <!--      v-model="selectedDate"-->
      <!--      type="date"-->
      <!--      placeholder="请选择日期"-->
      <!--      value-format="YYYY-MM-DD"-->
      <!--      :disabled-date="disabledDate"-->
      <!--      clearable-->
      <!--      style="width: 200px"-->
      <!--  />-->
      <!--</el-form-item>-->

      <!--<el-form-item label="开始时间" prop="useStartTime" v-hasPermi="['room:room:reserve']" label-width="100px">-->
      <!--  <el-time-select-->
      <!--      style="width: 150px"-->
      <!--      v-model="useStartTime"-->
      <!--      :max-time="useEndTime"-->
      <!--      placeholder="开始时间"-->
      <!--      start="08:00"-->
      <!--      end="21:00"-->
      <!--      step="1:00"-->
      <!--      clearable-->
      <!--  />-->
      <!--</el-form-item>-->

      <!--<el-form-item label="结束时间" prop="useEndTime" v-hasPermi="['room:room:reserve']" label-width="100px">-->
      <!--  <el-time-select-->
      <!--      style="width: 150px"-->
      <!--      v-model="useEndTime"-->
      <!--      :min-time="useStartTime"-->
      <!--      placeholder="结束时间"-->
      <!--      start="08:00"-->
      <!--      end="21:00"-->
      <!--      step="1:00"-->
      <!--      clearable-->
      <!--  />-->
      <!--</el-form-item>-->

      <el-form-item label="座位数" prop="seatingCapacity" label-width="100px">
        <el-input
            v-model="queryParams.seatingCapacity"
            placeholder="请输入座位数"
            clearable
            style="width: 200px"

        />
      </el-form-item>
      <el-form-item label="设备" prop="equipment" label-width="100px">
        <el-input
            v-model="queryParams.equipment"
            placeholder="请输入多媒体设备"
            clearable
            style="width: 200px"

        />
      </el-form-item>
      <el-form-item label="租赁价格" prop="price" label-width="100px">
        <el-input
            v-model="queryParams.price"
            placeholder="请输入每小时租赁价格"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="会议室类型" prop="roomStatus" label-width="100px">
        <el-select
            v-model="queryParams.roomType"
            placeholder="请选择会议室类型"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
        >
          <el-option
              v-for="dict in room_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="roomStatus" label-width="100px">
        <el-select
            v-model="queryParams.roomStatus"
            placeholder="请选择会议室状态"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
        >
          <el-option
              v-for="dict in room_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain
            icon="Plus"
            @click="handleAdd"
            v-hasPermi="['room:room:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            icon="Edit"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['room:room:edit']"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['room:room:remove']"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="warning"
            plain
            icon="Download"
            @click="handleExport"
            v-hasPermi="['room:room:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="roomList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="序号" align="center" type="index" width="50vw"/>
      <el-table-column label="会议室名称" align="center" prop="roomName"/>
      <el-table-column label="会议室类型" align="center" prop="roomType"/>
      <el-table-column label="座位数" align="center" prop="seatingCapacity"/>
      <el-table-column label="多媒体设备" align="center" prop="equipment"/>
      <el-table-column label="租赁价格" align="center" prop="price"/>
      <el-table-column label="状态" align="center" prop="roomStatus">
        <template #default="scope">
          <dict-tag :options="room_status" :value="scope.row.roomStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['room:room:edit']"
                     v-if="useUserStore().roles.includes('manager')||useUserStore().roles.includes('admin')">修改
          </el-button>
          <el-select placeholder="修改状态"
                     v-model="scope.row.roomStatus"
                     style="width: 100px;"
                     v-hasPermi="['room:room:edit']"
                     @change="(value) => handleStatusChange(value, scope.row)">
            <el-option
                v-for="dict in room_status"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            ></el-option>
          </el-select>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['room:room:remove']">删 除
          </el-button>
          <el-button v-if="scope.row.roomStatus ==='vacancy' && useUserStore().roles.includes('guest')" link
                     type="primary" icon="Plus" @click="handleReserve(scope.row)"
                     v-hasPermi="['room:room:reserve']">预 定
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
        v-show="total>0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
    />

    <!-- 添加或修改会议室基础信息，记录公司所有会议室的配置及状态对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="roomRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="会议室名称" prop="roomName">
          <el-input v-model="form.roomName" placeholder="请输入会议室名称" style="width: 240px"/>
        </el-form-item>
        <el-form-item label="最大座位数" prop="seatingCapacity">
          <el-input v-model="form.seatingCapacity" placeholder="请输入最大座位数" style="width: 240px"/>
        </el-form-item>
        <el-form-item label="会议室类型" prop="roomType">
          <el-select v-model="form.roomType" placeholder="请选择会议室类型" style="width: 240px">
            <el-option
                v-for="roomType in queryParams.meetingRoomTypes"
                :label="roomType"
                :value="roomType">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="多媒体设备列表" prop="equipment">
          <el-input v-model="form.equipment" placeholder="请输入多媒体设备列表" style="width: 240px"/>
        </el-form-item>
        <el-form-item label="每小时租赁价格" prop="price">
          <el-input v-model="form.price" placeholder="请输入每小时租赁价格" style="width: 240px"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
    <!--    预订会议室对话框-->
    <el-dialog :title="title" v-model="reserveDialog" width="500px" append-to-body>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="会议室名称">{{ form.roomName }}</el-descriptions-item>
        <el-descriptions-item label="最大座位数">{{ form.seatingCapacity }}</el-descriptions-item>
        <el-descriptions-item label="会议室类型">{{ form.roomType }}</el-descriptions-item>
        <el-descriptions-item label="多媒体设备">{{ form.equipment }}</el-descriptions-item>
        <el-descriptions-item label="预约日期">
          <el-date-picker
              v-model="selectedDate"
              type="date"
              placeholder="请选择日期"
              value-format="YYYY-MM-DD"
              :disabled-date="disabledDate"
              clearable
              style="width: 200px"
          />
        </el-descriptions-item>
        <el-descriptions-item label="预约时间段">
          <el-time-select
              style="width: 150px"
              v-model="useStartTime"
              :max-time="useEndTime"
              placeholder="开始时间"
              start="08:00"
              end="21:00"
              step="1:00"
              clearable
          />
          ~
          <el-time-select
              style="width: 150px"
              v-model="useEndTime"
              :min-time="useStartTime"
              placeholder="结束时间"
              start="08:00"
              end="21:00"
              step="1:00"
              clearable
          />
        </el-descriptions-item>
        <el-descriptions-item label="预定时长">
          {{ getTimeDifference(useStartTime, useEndTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="每小时租赁价格">{{ form.price }}</el-descriptions-item>
        <el-descriptions-item label="合计价格">{{
            getTotalPrice(useStartTime, useEndTime, form.price)
          }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleCreateOrder()">确 定</el-button>
          <el-button @click="()=>reserveDialog=!reserveDialog">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {listRoom, getRoom, delRoom, addRoom, updateRoom, listIdleRoom} from "@/api/meetingroom/room";
import useUserStore from "@/store/modules/user.js";
import {addOrders} from "@/api/order/orders.js";

const {proxy} = getCurrentInstance();
const {room_status} = proxy.useDict("room_status");
const {room_type} = proxy.useDict("room_type");
const roomList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

const selectedDate = ref('');   // 日期部分
const useStartTime = ref(''); // 新增：开始时间
const useEndTime = ref('');   // 新增：结束时间
const reserveDialog = ref(false);
const reserveFlag = ref(false);
const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    roomName: null,
    seatingCapacity: null,
    usageTime: null,
    equipment: null,
    price: null,
    roomType: null,
    roomStatus: null,
    meetingRoomTypes: ['普通会议室', '教室型', '圆桌型']
  },
  rules: {
    roomName: [
      {required: true, message: "会议室名称不能为空", trigger: "blur"}
    ],
    seatingCapacity: [
      {required: true, message: "最大座位数不能为空", trigger: "blur"}
    ],
    price: [
      {required: true, message: "每小时租赁价格不能为空", trigger: "blur"}
    ],
    roomStatus: [
      {required: true, message: "状态说明：vacancy=空闲不能为空", trigger: "change"}
    ]
  },
});

const {queryParams, form, rules} = toRefs(data);

// 要禁止选择今天之前的日期和60天以后的日期
const disabledDate = (time) => {
  const now = new Date();
  return time.getTime() < now.getTime() - 24 * 60 * 60 * 1000 || time.getTime() > now.getTime() + 59 * 24 * 60 * 60 * 1000;
};


watch(() => queryParams.value.useTimeRange, (newVal) => {
  if (newVal && newVal.length === 2) {
    queryParams.value.useStartTime = newVal[0];
    queryParams.value.useEndTime = newVal[1];
  } else {
    queryParams.value.useStartTime = null;
    queryParams.value.useEndTime = null;
  }
});
listRoom(queryParams.value).then(response => {
  roomList.value = response.rows;
  total.value = response.total;
  loading.value = false;
});


/** 查询会议室基础信息，记录公司所有会议室的配置及状态列表 */
function getList() {
  loading.value = true;
  listRoom(queryParams.value).then(response => {
    roomList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 查询空闲的会议室基础信息*/
function getIdleRoomList() {
  loading.value = true;
  listIdleRoom(queryParams.value).then(response => {
    roomList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    roomId: null,
    roomName: null,
    roomType: null,
    seatingCapacity: null,
    equipment: null,
    price: null,
    roomStatus: null,
  };
  proxy.resetForm("roomRef");
}

/** 搜索按钮操作 */
// 根据selectedDate.value && useStartTime.value && useEndTime.value是否为空分为客户查询和其他角色查询
function handleQuery() {
  queryParams.value.pageNum = 1;
  // 客户查询会议室进行筛选
  // if (useUserStore().roles.includes('guest')) {
  //   if (selectedDate.value && useStartTime.value && useEndTime.value) {
  //     queryParams.value.usageTime = `${selectedDate.value}_${useStartTime.value}_${useEndTime.value}`
  //     getIdleRoomList();
  //     reserveFlag.value = true;
  //   } else {
  //     proxy.$message.warning('请选择日期和时间');
  //   }
  // } else {//其他角色
  //   getList();
  // }
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  queryParams.value.useTimeRange = null;
  queryParams.value.useStartTime = null;
  queryParams.value.useEndTime = null;
  handleQuery();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.roomId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加会议室相关信息，记录所有会议室的配置及状态";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _roomId = row.roomId || ids.value
  getRoom(_roomId).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改会议室相关信息，记录所有会议室的配置及状态";
  });
}

function handleStatusChange(value, row) {
  const updateData = {
    roomId: row.roomId,
    roomStatus: value
  };

  updateRoom(updateData).then(response => {
    proxy.$modal.msgSuccess("状态更新成功");
  }).catch(() => {
    proxy.$modal.msgError("状态更新失败");
  });
  getList()
}
/** 提交按钮 */
function submitForm() {
  proxy.$refs["roomRef"].validate(valid => {
    if (valid) {
      if (form.value.roomId != null) {
        updateRoom(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addRoom(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _roomIds = row.roomId || ids.value;
  proxy.$modal.confirm('是否确认删除会议室基础信息，记录公司所有会议室的配置及状态编号为"' + _roomIds + '"的数据项？').then(function () {
    return delRoom(_roomIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {
  });
}

function formatReservationTime() {
  const date = selectedDate.value;
  const start = useStartTime.value;
  const end = useEndTime.value;
  if (!date || !start || !end) {
    return null; // 或者返回提示信息："日期或时间未填写完整"
  }
  return `${date}_${start}_${end}`;
}

function handleCreateOrder() {
  // 判断日期，时间段不能为空
  if (!selectedDate.value || !useStartTime.value || !useEndTime.value) {
    proxy.$message.warning("日期或时间段不能为空w(ﾟДﾟ)w")
    return
  }
  form.value.usageTime = formatReservationTime();
  form.value.price = getTotalPrice(useStartTime.value, useEndTime.value, form.value.price);
  form.value.userId = useUserStore().id;
  // 修改会议室状态为锁定
  updateRoom({
    roomId: form.value.roomId,
    roomStatus: 'locked'
  }).then(response => {
  });
  addOrders(form.value).then(response => {
    proxy.$modal.msgSuccess("订单创建成功");
    open.value = false;
    getList();
  });
  reserveDialog.value = false;
}

/*预定按钮操作*/
function handleReserve(row) {
  form.value = row;
  reserveDialog.value = true;
  title.value = "请确认订单";
}

// 计算时间差
function getTimeDifference(start, end) {
  if (!start || !end) return "";

  const startDate = new Date(`1970-01-01T${start}:00`);
  const endDate = new Date(`1970-01-01T${end}:00`);

  const diffMs = endDate - startDate;
  if (diffMs <= 0) return "时间不合法";

  const hours = Math.floor(diffMs / (1000 * 60 * 60));
  const minutes = Math.floor((diffMs / (1000 * 60)) % 60);

  return `${hours}小时${minutes}分钟`;
}

function getTotalPrice(start, end, pricePerHour) {
  if (!start || !end || !pricePerHour || isNaN(pricePerHour)) return "0.00";

  const startDate = new Date(`1970-01-01T${start}:00`);
  const endDate = new Date(`1970-01-01T${end}:00`);

  const diffMs = endDate - startDate;
  if (diffMs <= 0) return "时间不合法";

  const totalHours = diffMs / (1000 * 60 * 60); // 转换为小时（含小数）
  const totalPrice = totalHours * parseFloat(pricePerHour);

  return totalPrice.toFixed(2); // 保留两位小数
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('meetingroom/room/export', {
    ...queryParams.value
  }, `room_${new Date().getTime()}.xlsx`)
}

getList();
</script>
