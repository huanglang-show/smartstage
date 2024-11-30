<template>
  <div id="adminApp">
    <a-table
      :columns="columns"
      :data="dataList"
      :pagination="{
        showTotal: true,
        pageSize: searchParams.pageSize,
        current: searchParams.current,
        total,
      }"
      @page-change="onPageChange"
    >
      <template #index="{ rowIndex }">
        {{ rowIndex + 1 }}
      </template>
      <template #appIcon="{ record }">
        <!--        <a-image width="64" :src="record.userAvatar" />-->
        <!--        写死用于测试-->
        <img width="40px" src="../../../public/labi.jpeg" alt="logo" />
      </template>
      <template #appType="{ record }">
        <div v-if="record.appType === 1">测评类</div>
        <div v-else>得分类</div>
      </template>
      <template #scoringStrategy="{ record }">
        <div v-if="record.scoringStrategy === 1">AI</div>
        <div v-else>自定义</div>
      </template>
      <template #reviewStatus="{ record }">
        <div v-if="record.reviewStatus === 1">通过</div>
        <div v-else-if="record.reviewStatus === 0">待审核</div>
        <div v-else>拒绝</div>
      </template>
      <template #userName="{ record }">
        <div>{{ record.user.userName }}</div>
      </template>

      <template #optional="{ record }">
        <a-space>
          <a-button type="secondary" @click="doUpdate(record)">修改</a-button>
          <a-button status="danger" @click="doDelete(record.id)">删除</a-button>
        </a-space>
      </template>
    </a-table>

    <!--    修改模态框-->
    <admin-update-user-modal-view
      :is-modal-visible="isModalVisible"
      :user-data="selectRecord"
      @handleUpdateUser="handleUpdateUser"
      @cancelModal="cancelModal"
    />
  </div>
</template>
<script setup lang="ts">
import { ref, watchEffect } from "vue";
import {
  deleteUserUsingPost,
  listUserByPageUsingPost,
  updateUserUsingPost,
} from "@/api/userController";
import API from "@/api/typings";
import AdminUpdateUserModalView from "@/views/admin/AdminUpdateUserModalView.vue";
import message from "@arco-design/web-vue/es/message";
import { listAppByPageUsingPost } from "@/api/appController";

// 模态框展示与否
const isModalVisible = ref(false);
// 当前选中记录
const selectRecord = ref<API.AppVO | null>(null);

// 数据总条数
const total = ref(0);
const dataList = ref<API.AppVO[]>([]);
/**
 * 初始化搜索分页条件
 */
const initSearchParams = ref({
  pageSize: 10,
  current: 1,
});
const searchParams = ref<API.AppQueryRequest>({
  ...initSearchParams.value,
});

/**
 * 加载数据
 */
const loadData = async () => {
  await listAppByPageUsingPost(searchParams.value).then((res) => {
    if (res.data.code === 0 && res.data.data) {
      dataList.value = res.data.data?.records || [];
      total.value = res.data.data?.total || 0;
    }
  });
};

/**
 * 删除
 * @param id
 */
const doDelete = async (id: number) => {
  await deleteUserUsingPost({ id }).then((res) => {
    if (res.data.code === 0) {
      loadData();
    }
  });
};

/**
 * 修改
 * @param record
 */
const doUpdate = (record: API.User) => {
  selectRecord.value = record;
  isModalVisible.value = true;
};
const handleUpdateUser = async (updateUser: API.User) => {
  await updateUserUsingPost(updateUser).then((res) => {
    if (res.data.code === 0) {
      message.success("修改成功");
      isModalVisible.value = false;
      loadData();
    } else {
      message.error("修改失败");
    }
  });
};

/**
 * 关闭模态框
 * @param v
 */
const cancelModal = (v: boolean) => {
  isModalVisible.value = v;
};

const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};

// 防抖函数
function debounce(fn: Function, delay: number) {
  let timer: NodeJS.Timeout | null = null;
  return function (...args: any[]) {
    if (timer) clearTimeout(timer);
    timer = setTimeout(() => {
      fn.apply(this, args);
    }, delay);
  };
}

// 使用防抖函数包裹 loadData
const debouncedLoadData = debounce(loadData, 300);
/**
 * 监听 searchParams 变量，改变时触发数据的重新加载
 */
watchEffect(() => {
  debouncedLoadData();
});

/**
 * 表格列
 */
const columns = [
  // {
  //   title: null,
  //   dataIndex: "id",
  //   render: () => null,
  // },
  {
    title: "序号",
    dataIndex: "index",
    // scopedSlots: { customRender: "index" },
    slotName: "index",
  },
  {
    title: "应用名",
    dataIndex: "appName",
  },
  {
    title: "应用描述",
    dataIndex: "appDesc",
  },
  {
    title: "应用图标",
    dataIndex: "appIcon",
    slotName: "appIcon",
  },
  {
    title: "应用类型",
    dataIndex: "appType",
    slotName: "appType",
  },
  {
    title: "评分策略",
    dataIndex: "scoringStrategy",
    slotName: "scoringStrategy",
  },
  {
    title: "审核人",
    dataIndex: "reviewerName",
  },
  {
    title: "审核状态",
    dataIndex: "reviewStatus",
    slotName: "reviewStatus",
  },
  {
    title: "审核信息",
    dataIndex: "reviewMessage",
  },
  {
    title: "审核时间",
    dataIndex: "reviewTime",
    slotName: "reviewTime",
  },
  {
    title: "创建人",
    dataIndex: "user",
    slotName: "userName",
  },
  {
    title: "创建时间",
    dataIndex: "createTime",
    slotName: "createTime",
  },
  {
    title: "更新时间",
    dataIndex: "updateTime",
    slotName: "updateTime",
  },
  {
    title: "操作",
    slotName: "optional",
  },
];
</script>
<style scoped>
#adminUser {
}
</style>
