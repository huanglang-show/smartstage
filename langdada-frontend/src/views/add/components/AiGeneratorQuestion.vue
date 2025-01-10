<template>
  <a-button style="background-color: #59c9e3" @click="handleClick">
    AI生成题目
  </a-button>
  <a-drawer
    :width="320"
    :visible="visible"
    @ok="handleOk"
    @cancel="handleCancel"
    unmountOnClose
  >
    <template #title> AI生成题目</template>
    <div>
      <a-form :rules="rules" :model="form" @submit="handleSubmit">
        <a-form-item label="应用id:">
          {{ appId }}
        </a-form-item>
        <a-form-item field="questionNumber" label="题目数量:">
          <a-input-number
            min="0"
            max="20"
            v-model="form.questionNumber"
            placeholder="请输入题目数量..."
          />
        </a-form-item>
        <a-form-item field="optionNumber" label="选项数量:">
          <a-input-number
            min="0"
            max="20"
            v-model="form.optionNumber"
            placeholder="请输入选项数量..."
          />
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button v-if="submitting" type="outline " loading>
              生成中
            </a-button>
            <a-button v-if="!submitting" type="primary" html-type="submit">
              一键生成
            </a-button>
            <a-button v-if="!submitting" type="primary" @click="submitSSE">
              实时生成
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </div>
  </a-drawer>
</template>
<script setup lang="ts">
import { ref, withDefaults, defineProps, reactive } from "vue";
import API from "@/api/typings";
import { generateUsingPost } from "@/api/aiQuestionController";
import message from "@arco-design/web-vue/es/message";

interface Props {
  appId: string;
  onSuccess?: (result: API.QuestionFrameWork[]) => void;
  onSuccessSSE?: (result: API.QuestionFrameWork) => void;
  onSSEStart?: (event: any) => void;
  onSSEClose?: (event: any) => void;
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => {
    return "";
  },
});

const form = reactive({
  questionNumber: 10,
  optionNumber: 2,
} as API.AiGenerateQuestionRequest);

// 提交按钮状态
const submitting = ref(false);

const visible = ref(false);
const handleClick = () => {
  visible.value = true;
};
const handleOk = () => {
  visible.value = false;
};
const handleCancel = () => {
  visible.value = false;
};

/**
 * 提交
 */
const handleSubmit = async () => {
  if (!props.appId) {
    return;
  }
  submitting.value = true;
  const res = await generateUsingPost({
    appId: props.appId as any,
    ...form,
  });
  if (res.data.code === 0 && res.data?.data?.length > 0) {
    if (props.onSuccess) {
      props.onSuccess(res.data.data as API.QuestionFrameWork[]);
    }
    // 关闭抽屉
    handleCancel();
  } else {
    message.error("生成题目失败" + res.data.message);
  }
  submitting.value = false;
};
/**
 * sse 实时生成
 */
const submitSSE = async () => {
  if (!props.appId) {
    return;
  }
  submitting.value = true;
  // SSE请求
  const eventSource = new EventSource(
    // *需要填写完整的后端接口地址
    "http://localhost:8101/api/question/ai/generate/sse" +
      `?appId=${props.appId}&optionNumber=${form.optionNumber}&questionNumber=${form.questionNumber}`
  );
  let first = true;
  // 接收消息时触发
  eventSource.onmessage = function (event) {
    if (first) {
      props.onSSEStart?.(event);
      first = !first;
      handleCancel();
    }
    props.onSuccessSSE?.(JSON.parse(event.data));
  };
  // 连接报错或连接关闭时触发
  eventSource.onerror = function (event) {
    if (event.eventPhase === EventSource.CLOSED) {
      console.log("关闭连接");
      props.onSSEClose?.(event);
      eventSource.close();
    } else {
      eventSource.close();
    }
  };
  submitting.value = false;
};

const rules = {
  questionNumber: [
    {
      required: true,
      message: "请输入题目数量",
    },
  ],
  optionNumber: [
    {
      required: true,
      message: "请输入选项数量",
    },
  ],
};
</script>

<style scoped></style>
