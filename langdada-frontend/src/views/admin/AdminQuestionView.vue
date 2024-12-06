<template>
  <div id="adminQuestion">
    <a-form
      layout="inline"
      :model="fromSearchParams"
      @submit="doSerach"
      :style="{ marginBottom: '20px' }"
    >
      <a-form-item label="题目id" field="id">
        <a-input
          v-model="fromSearchParams.userId"
          placeholder="请输入用户id"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="题目内容" field="questionContent">
        <a-input
          v-model="fromSearchParams.questionContent"
          placeholder="请输入题目内容"
          allow-clear
        />
      </a-form-item>
      <a-form-item>
        <a-button html-type="submit" type="primary">搜索</a-button>
      </a-form-item>
    </a-form>
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

      <template #content="{ record }">
        <md-view :value="record.questionContent" />
      </template>
      <template #optional="{ record }">
        <a-space>
          <a-button type="secondary" @click="doUpdate(record)" disabled
            >修改
          </a-button>
          <a-button status="danger" @click="doDelete(record.id)">删除</a-button>
        </a-space>
      </template>
    </a-table>

    <!--    修改模态框-->
    <admin-update-app-modal-view
      :is-modal-visible="isModalVisible"
      :question-data="selectRecord"
      @handleUpdateQuestion="handleUpdateQuestion"
      @cancelModal="cancelModal"
    />
  </div>
</template>
<script setup lang="ts">
import { ref, watchEffect } from "vue";
import API from "@/api/typings";
import message from "@arco-design/web-vue/es/message";
import AdminUpdateAppModalView from "@/views/admin/AdminUpdateAppModalView.vue";
import {
  deleteQuestionUsingPost,
  editQuestionUsingPost,
  listQuestionByPageUsingPost,
} from "@/api/questionController";
import MdView from "@/components/MdView.vue";

// 模态框展示与否
const isModalVisible = ref(false);
// 当前选中记录
const selectRecord = ref<API.QuestionVO | null>(null);

// 数据总条数
const total = ref(0);
const dataList = ref<API.QuestionVO[]>([]);

const fromSearchParams = ref<API.QuestionQueryRequest>({});
/**
 * 初始化搜索分页条件
 */
const initSearchParams = ref({
  pageSize: 10,
  current: 1,
});
const searchParams = ref<API.QuestionQueryRequest>({
  ...initSearchParams.value,
});

/**
 * 加载数据
 */
const loadData = async () => {
  await listQuestionByPageUsingPost(searchParams.value).then((res) => {
    if (res.data.code === 0 && res.data.data) {
      dataList.value = res.data.data?.records || [];
      total.value = res.data.data?.total || 0;
    }
  });
};

const doSerach = () => {
  searchParams.value = {
    ...searchParams.value,
    ...fromSearchParams.value,
  };
};

watchEffect(() => {
  loadData();
});

/**
 * 删除
 * @param id
 */
const doDelete = async (id: number) => {
  await deleteQuestionUsingPost({ id }).then((res) => {
    if (res.data.code === 0) {
      loadData();
    }
  });
};

/**
 * 修改
 * @param record
 */
const doUpdate = (record: API.QuestionVO) => {
  selectRecord.value = record;
  isModalVisible.value = true;
};
const handleUpdateQuestion = async (
  updateQuestion: API.QuestionEditRequest
) => {
  await editQuestionUsingPost(updateQuestion).then((res) => {
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
    title: "内容",
    dataIndex: "content",
    slotName: "content",
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
<style scoped></style>
