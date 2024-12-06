<template>
  <div id="adminScoring">
    <a-form
      layout="inline"
      :model="fromSearchParams"
      @submit="doSerach"
      :style="{ marginBottom: '10px' }"
    >
      <a-form-item label="题目id" field="id">
        <a-input
          v-model="fromSearchParams.userId"
          placeholder="请输入用户id"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="应用id" field="appId">
        <a-input
          v-model="fromSearchParams.appId"
          placeholder="请输入应用id"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="结果名称" field="resultName">
        <a-input
          v-model="fromSearchParams.resultName"
          placeholder="请输入结果名称"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="结果描述" field="resultProp">
        <a-input
          v-model="fromSearchParams.resultProp"
          placeholder="请输入结果描述"
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
      <template #resultPicture="{ record }">
        <img
          alt="logo"
          src="../../../public/labi.jpeg"
          width="50"
          height="50"
        />
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
      @handleUpdateQuestion="handleUpdateScoring"
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
import {
  deleteScoringResultUsingPost,
  editScoringResultUsingPost,
  listScoringResultByPageUsingPost,
} from "@/api/scoringResultController";

// 模态框展示与否
const isModalVisible = ref(false);
// 当前选中记录
const selectRecord = ref<API.ScoringResultVO | null>(null);

// 数据总条数
const total = ref(0);
const dataList = ref<API.ScoringResult[]>([]);

const fromSearchParams = ref<API.ScoringResultQueryRequest>({});
/**
 * 初始化搜索分页条件
 */
const initSearchParams = ref({
  pageSize: 10,
  current: 1,
});
const searchParams = ref<API.ScoringResultQueryRequest>({
  ...initSearchParams.value,
});

/**
 * 加载数据
 */
const loadData = async () => {
  await listScoringResultByPageUsingPost(searchParams.value).then((res) => {
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
  await deleteScoringResultUsingPost({ id }).then((res) => {
    if (res.data.code === 0) {
      loadData();
    }
  });
};

/**
 * 修改
 * @param record
 */
const doUpdate = (record: API.ScoringResultVO) => {
  selectRecord.value = record;
  isModalVisible.value = true;
};
const handleUpdateScoring = async (
  updateScoring: API.ScoringResultEditRequest
) => {
  await editScoringResultUsingPost(updateScoring).then((res) => {
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
    title: "结果名称",
    dataIndex: "resultName",
    slotName: "resultName",
  },
  {
    title: "结果描述",
    dataIndex: "resultDesc",
    slotName: "resultDesc",
  },
  {
    title: "结果图片",
    dataIndex: "resultPicture",
    slotName: "resultPicture",
  },
  {
    title: "结果属性",
    dataIndex: "resultProp",
    slotName: "resultProp",
  },
  {
    title: "结果得分",
    dataIndex: "resultScoreRange",
    slotName: "resultScoreRange",
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
