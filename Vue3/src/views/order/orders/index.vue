<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryRef" :inline="true" :model="queryParams" label-width="68px">
      <el-form-item label="会议室" prop="roomName">
        <el-input
            v-model="queryParams.roomName"
            clearable
            placeholder="请输入会议室"
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item v-if="!useUserStore().roles.includes('guest')" label="用户" prop="userName">
        <el-input
            v-model="queryParams.userName"
            clearable
            placeholder="请输入用户名"
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单金额" prop="price">
        <el-input
            v-model="queryParams.price"
            clearable
            placeholder="请输入订单金额"
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button icon="Search" type="primary" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            v-hasPermi="['order:orders:add']"
            icon="Plus"
            plain
            type="primary"
            @click="handleAdd"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            v-hasPermi="['order:orders:edit']"
            :disabled="single"
            icon="Edit"
            plain
            type="success"
            @click="handleUpdate"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            v-hasPermi="['order:orders:remove']"
            :disabled="multiple"
            icon="Delete"
            plain
            type="danger"
            @click="handleDelete"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            v-hasPermi="['order:orders:export']"
            icon="Download"
            plain
            type="warning"
            @click="handleExport"
        >导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="ordersList" @selection-change="handleSelectionChange">
      <el-table-column align="center" type="selection" width="55"/>
      <el-table-column align="center" label="序号" type="index" width="50vw"/>
      <el-table-column align="center" label="会议室" prop="roomName"/>
      <el-table-column align="center" label="用户" prop="userName"/>
      <el-table-column align="center" label="订单金额" prop="price"/>
      <el-table-column align="center" label="创建时间" prop="createTime"/>
      <el-table-column align="center" label="使用时间" prop="usageTime">
        <template #default="scope">
          <div :key="scope.row.orderId"> <!-- 添加唯一 key -->
            {{ scope.row.usageTime.split('_')[0] }}
            {{ scope.row.usageTime.split('_')[1] }} ~
            {{ scope.row.usageTime.split('_')[2] }}
          </div>
        </template>
      </el-table-column>
      >
      <el-table-column align="center" label="订单状态" prop="orderStatus">
        <template #default="scope">
          <dict-tag :options="order_status" :value="scope.row.orderStatus"/>
        </template>
      </el-table-column>
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作">
        <template #default="scope">
          <el-button v-show="scope.row.orderStatus==='已取消'" v-hasPermi="['order:orders:pay_unsubscribe']" icon="Search"
                     link
                     type="primary" @click="confirmDialog(scope.row)">确 认
          </el-button>
          <el-button  v-show="scope.row.orderStatus==='已创建'" v-hasPermi="['order:orders:pay_unsubscribe']" icon="Check"
                     link
                     type="primary" @click="payDialog(scope.row)">支 付
          </el-button>
          <el-button  v-show="scope.row.orderStatus==='等待使用'" v-hasPermi="['order:orders:pay_unsubscribe']" icon="Star"
                     link
                     type="primary" @click="unsubscribeDialog(scope.row)">退 订
          </el-button>
          <el-button  v-show="scope.row.orderStatus==='待审核'" v-hasRole="['admin','employee']" icon="Star" link
                     type="primary" @click="checkDialog(scope.row)">审 核
          </el-button>
          <el-button v-hasPermi="['order:orders:edit']" icon="Edit" link type="primary"
                     @click="handleUpdate(scope.row)">修 改
          </el-button>
          <el-button v-hasPermi="['order:orders:remove']" icon="Delete" link type="primary"
                     @click="handleDelete(scope.row)">删 除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
        v-show="total>0"
        v-model:limit="queryParams.pageSize"
        v-model:page="queryParams.pageNum"
        :total="total"
        @pagination="getList"
    />
    <!--支付对话框-->
    <el-dialog v-model="openPay" :title="title"  width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="订单金额">{{ form.price }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handlePay()">去支付</el-button>
          <el-button @click="()=>openPay=!openPay">取 消</el-button>
        </div>
      </template>
    </el-dialog>
    <!--退款或审核对话框-->
    <el-dialog v-model="openUnsubscribeOrCheck" :title="title"  width="600px" center>
      <el-form label-position="right" label-width="90px" class="unsubscribe-dialog-form">
        <el-alert type="info" show-icon :closable="false" class="unsubscribe-info">
          <p>若放弃使用会议室，需要提前 24 小时申请取消预约。</p>
          <p>退费规则：</p>
          <ol>
            <li>提前 72 小时退全款</li>
            <li>提前 48 小时退 75%</li>
            <li>提前 24 小时退 25%</li>
          </ol>
          <p>退费流程：(括号内为流程成功时订单的状态)</p>
          <p>客户退订（待审核）->员工审核（已取消）->客户确认（已完成）->退款到账</p>
        </el-alert>

        <div class="form-fields">
          <el-form-item label="退款金额">
            <el-input disabled v-model="form.refundPrice" placeholder="退款金额">
              <template #append>元</template>
            </el-input>
          </el-form-item>

          <el-form-item label="退订原因">
            <el-input :disabled="form.orderStatus==='待审核'" v-model="form.refundReason" placeholder="请输入退款原因"
                      type="textarea" :rows="4"/>
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleUnsubscribe()" v-if="form.orderStatus==='等待使用'">退 款</el-button>
          <el-button @click="()=>openUnsubscribeOrCheck=false" v-if="form.orderStatus==='等待使用'">取 消</el-button>
          <el-button type="primary" @click="handlePass()" v-if="form.orderStatus==='待审核'">通 过</el-button>
          <el-button @click="handleReject()" v-if="form.orderStatus==='待审核'">拒 绝</el-button>
        </div>
      </template>
    </el-dialog>
    <!--最终确认对话框-->
    <el-dialog v-model="openConfirm" :title="title"  width="600px" center>
      <el-form label-position="right" label-width="90px" class="unsubscribe-dialog-form">
        <div class="form-fields">
          <el-form-item label="会议室">
            <el-input v-model="form.roomName" placeholder="会议室" disabled/>
          </el-form-item>
          <el-form-item label="退款金额">
            <el-input disabled v-model="form.refundPrice" placeholder="退款金额">
              <template #append>元</template>
            </el-input>
          </el-form-item>
          <el-form-item label="退订原因">
            <el-input disabled v-model="form.refundReason" placeholder="退款原因"
                      type="textarea" :rows="4"/>
          </el-form-item>
        </div>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleConfirmRefund()">确认退款</el-button>
          <el-button @click="handleRejectRefund()">取消退款</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 添加或修改会议室订单对话框 -->
    <el-dialog v-model="open" :title="title" append-to-body width="500px">
      <el-form ref="ordersRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="会议室">
          <el-select v-model="form.roomId" placeholder="请选择会议室">
            <el-option
                v-for="room in ordersList"
                :key="room.roomId"
                :label="room.roomName"
                :value="room.roomId">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="用户" prop="userName">
          <el-select v-model="form.userId" placeholder="请选择用户">
            <el-option
                v-for="room in ordersList"
                :key="room.userId"
                :label="room.userName"
                :value="room.userId">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="订单金额" prop="price">
          <el-input v-model="form.price" placeholder="请输入订单金额"/>
        </el-form-item>
        <el-form-item label="订单状态" prop="orderStatus">
          <el-select v-model="form.orderStatus" placeholder="请选择订单状态">
            <el-option
                v-for="orderStatus in order_status"
                :key="orderStatus.value"
                :label="orderStatus.label"
                :value="orderStatus.value">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {
  listOrders,
  listUser,
  getOrders,
  delOrders,
  addOrders,
  updateOrders,
  payOrder,
  getRefund, postUnsubscribe
} from "@/api/order/orders";
import useUserStore from "@/store/modules/user.js";
import {listRoom, updateRoom} from "@/api/meetingroom/room.js";

const {proxy} = getCurrentInstance();
const {order_status} = proxy.useDict('order_status');
const ordersList = ref([]);
// const roomList = ref([]);
// const userList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const openPay = ref(false);
const openUnsubscribeOrCheck = ref(false);
const openConfirm = ref(false);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    roomId: null,
    roomName: null,
    userId: null,
    userName: null,
    price: null,
    orderStatus: null,
    refundReason:null,
    refundPrice:null
  },
  rules: {
    roomId: [
      {required: true, message: "会议室名称不能为空", trigger: "blur"}
    ],
    userId: [
      {required: true, message: "用户名称不能为空", trigger: "blur"}
    ],
    price: [
      {required: true, message: "订单金额不能为空", trigger: "blur"}
    ],
    createTime: [
      {required: true, message: "下单时间不能为空", trigger: "blur"}
    ]
  }
});

const {queryParams, form, rules} = toRefs(data);

/** 查询会议室订单列表 */
function getList() {
  loading.value = true;

  if (useUserStore().roles.includes('guest')) {
    queryParams.value.userId = useUserStore().id;
    // queryParams.value.userName = useUserStore().name;
    // alert("判断为真"+useUserStore().id);
  }
  listOrders(queryParams.value).then(response => {
    ordersList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}


/** 查询用户列表 */
// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    orderId: null,
    roomId: null,
    userId: null,
    price: null,
    orderStatus: null,
    createTime: null,
    usageTime: null
  };
  proxy.resetForm("ordersRef");
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.orderId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加会议室订单";
}

// 显示支付订单对话框
function payDialog(row) {
  openPay.value = true;
  form.value = row;
}

// 显示最终确认订单对话框
function confirmDialog(row) {
  title.value = "最终确认订单";
  openConfirm.value = true;
  form.value = row;
}

// 显示退订订单对话框
function unsubscribeDialog(row) {
  title.value = "退订订单";
  openUnsubscribeOrCheck.value = true;
  getRefund(row.orderId).then(response => {
    form.value = {
      ...row,
      refundPrice: response,
    };
    // console.log(JSON.stringify(form.value))
  });
}

// 显示审核订单对话框
function checkDialog(row) {
  title.value = "审核订单";
  openUnsubscribeOrCheck.value = true;
  form.value = row
}

/** 支付按钮操作 */
function handlePay() {
  payOrder(form.value.orderId).then(response => {
    proxy.$modal.msgSuccess("支付成功");
    openPay.value = false;
    getList();
  });
}

/** 退订按钮操作 */
function handleUnsubscribe() {
  postUnsubscribe(form.value).then(response => {
    proxy.$modal.msgSuccess("退订成功");
    openUnsubscribeOrCheck.value = false;
    getList();
  });
}

/** 审核通过按钮操作 */
function handlePass() {
  updateOrders({orderId: form.value.orderId, orderStatus: '已取消'}).then(response => {
      proxy.$modal.msgSuccess("审核成功");
    getList();
    openUnsubscribeOrCheck.value = false;
  })
}

/** 驳回按钮操作 */
function handleReject() {
  // 弹窗提醒是否确认要拒绝
  proxy.$modal.confirm('是否确认要驳回该订单？').then(function () {
    updateOrders({orderId: form.value.orderId, orderStatus: '等待使用'}).then(response => {
      proxy.$modal.msgSuccess("驳回成功，请联系客户进行商讨");
      getList();
      openUnsubscribeOrCheck.value = false;
    })
  })
}

/** 确认退款按钮操作 */
function handleConfirmRefund() {
  proxy.$modal.confirm('是否确认要退款？').then(function () {
    console.log(JSON.stringify(form.value))
    updateOrders({orderId: form.value.orderId, orderStatus: '已完成'}).then(response => {
      updateRoom({roomId: form.value.roomId, roomStatus: 'vacancy'}).then(response => {
        proxy.$modal.notifySuccess("金额" + form.value.refundPrice + "退款成功，请注意查收，祝您生活愉快！");
        openConfirm.value = false;
        getList();
      })
    })
  })
}

/** 不退订按钮操作 */
function handleRejectRefund() {
  proxy.$modal.confirm('是否要取消退款？').then(function () {
    updateOrders({orderId: form.value.orderId, orderStatus: '等待使用'}).then(response => {
      proxy.$message.success("取消退款成功！")
      openConfirm.value = false;
      getList();
    })
  })
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _orderId = row.orderId || ids.value
  getOrders(_orderId).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改会议室订单";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["ordersRef"].validate(valid => {
    if (valid) {
      if (form.value.orderId != null) {
        updateOrders(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addOrders(form.value).then(response => {
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
  const _orderIds = row.orderId || ids.value;
  proxy.$modal.confirm('是否确认删除会议室订单编号为"' + _orderIds + '"的数据项？').then(function () {
    return delOrders(_orderIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {
  });
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('order/orders/export', {
    ...queryParams.value
  }, `orders_${new Date().getTime()}.xlsx`)
}

getList();
</script>

<style scoped>
.unsubscribe-dialog-form {
  padding: 0 20px;
}

.unsubscribe-info {
  margin-bottom: 20px;
}

.unsubscribe-info ol {
  padding-left: 20px;
  margin: 10px 0;
}

.form-fields {
  padding: 15px;
  border-radius: 4px;
}

.dialog-footer {
  text-align: center;
}

.dialog-footer .el-button {
  min-width: 100px;
  margin: 0 10px;
}

</style>