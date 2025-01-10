<template>
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
        src="../../../../public/labi.jpeg"
        width="50"
        height="50"
      />
    </template>
    <template #optional="{ record }">
      <a-space>
        <a-button type="secondary" @click="doUpdate(record)">修改</a-button>
        <a-button status="danger" @click="doDelete(record.id)">删除</a-button>
      </a-space>
    </template>
  </a-table>
</template>
<script setup lang="ts">
import { ref, watchEffect, withDefaults, defineProps, defineExpose } from "vue";
import API from "@/api/typings";
import MdView from "@/components/MdView.vue";
import {
  deleteScoringResultUsingPost,
  listScoringResultVoByPageUsingPost,
} from "@/api/scoringResultController";
import message from "@arco-design/web-vue/es/message";

interface Props {
  appId: string;
  doUpdate: (scoringResult: API.ScoringResultVO) => void;
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => {
    return "";
  },
});

// 数据总条数
const total = ref(0);
const dataList = ref<API.ScoringResultVO[]>([]);

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
  if (!props.appId) {
    return;
  }
  const params = {
    ...searchParams.value,
    appId: props.appId as any,
  };
  const res = await listScoringResultVoByPageUsingPost(params);
  if (res.data.code === 0 && res.data.data) {
    dataList.value = res.data.data?.records || [];
    total.value = res.data.data?.total || 0;
  } else {
    message.error("获取数据失败" + res.data.message);
  }
};

// 暴露函数给父组件
defineExpose({
  loadData,
});

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

const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};

/**
 * 监听 searchParams 变量，改变时触发数据的重新加载
 */
watchEffect(() => {
  loadData();
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
