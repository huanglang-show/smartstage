<template>
  <div id="doAnswerView">
    <a-card>
      <h1>{{ app.appName }}</h1>
      <p>{{ app.appDesc }}</p>
      <h2 style="margin-bottom: 16px">
        {{ currentNumber }}、{{ currentQuestion?.title }}
      </h2>
      <a-radio-group
        v-model="currentAnswer"
        :options="options"
        direction="vertical"
        @change="doChangeRadio"
      />
      <div style="margin-top: 24px">
        <a-space size="'large'">
          <a-button
            v-if="currentNumber > 1"
            type="primary"
            @click="lastQuestion"
            >上一题
          </a-button>
          <a-button
            v-if="currentNumber < questionContent.length"
            type="primary"
            @click="nextQuestion"
            >下一题
          </a-button>
          <a-button
            type="primary"
            v-if="currentNumber === questionContent.length"
            :loading="submitting"
            @click="submitAnswer"
            >提交
          </a-button>
        </a-space>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import API from "@/api/typings";
import {
  computed,
  ref,
  withDefaults,
  defineProps,
  watchEffect,
  reactive,
} from "vue";
import { getAppVoByIdUsingGet } from "@/api/appController";
import { useRouter } from "vue-router";
import message from "@arco-design/web-vue/es/message";
import { listQuestionVoByPageUsingPost } from "@/api/questionController";
import { addUserAnswerUsingPost } from "@/api/userAnswerController";

interface Props {
  appId: string;
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => {
    return "";
  },
});
const router = useRouter();

// 应用
const app = ref<API.AppVO>({});
// 题目内容结构（理解为题目列表）
const questionContent = ref<API.QuestionFrameWork[]>([]);
// 当前问题
const currentQuestion = ref<API.QuestionFrameWork>({});
// 当前问题的所有选项
const options = computed(() => {
  return currentQuestion.value?.options
    ? currentQuestion.value?.options.map((option) => ({
        label: `${option.key}.${option.value}`,
        value: option.key,
      }))
    : [];
});
// 当前问题序号
const currentNumber = ref<number>(1);
// 当前答案
const currentAnswer = ref<string>();
// 历史答案列表
const historyAnswers = reactive<string[]>([]);
// 提交中
const submitting = ref<boolean>(false);
/**
 * 上一题
 */
const lastQuestion = () => {
  if (currentNumber.value > 1) {
    currentNumber.value--;
  }
};
/**
 * 下一题
 */
const nextQuestion = () => {
  if (!currentAnswer.value) {
    message.info("请选择答案");
    return;
  }
  if (currentNumber.value < questionContent.value.length) {
    currentNumber.value++;
  }
};
/**
 * 提交
 */
const submitAnswer = async () => {
  if (!props.appId || !historyAnswers) {
    return;
  }
  submitting.value = true;
  const res = await addUserAnswerUsingPost({
    appId: props.appId as any,
    choices: historyAnswers,
  });
  if (res.data.code === 0) {
    message.success("提交成功");
  }
  submitting.value = false;
  // await router.push({ name: "answerResult", params: { appId: props.appId } });
};

/**
 * 单选框选择时
 * @param value
 */
const doChangeRadio = (value: string) => {
  historyAnswers[currentNumber.value - 1] = value;
  console.log("historyAnswers", historyAnswers);
};
/**
 * 加载数据
 */
const loadData = async () => {
  if (!props.appId) {
    return;
  }
  let res: any = await getAppVoByIdUsingGet({
    id: props.appId as any,
  });
  if (res.data.code === 0) {
    app.value = res.data.data;
  } else {
    message.error("获取数据失败" + res.data.message);
  }
  res = await listQuestionVoByPageUsingPost({
    appId: props.appId as any,
    current: 1,
    pageSize: 1,
    sortField: "createTime",
    sortOrder: "desc",
  });
  if (res.data.code === 0 && res.data.data?.records) {
    questionContent.value = res.data.data.records[0].content;
  } else {
    message.error("获取题目失败" + res.data.message);
  }
};

// 获取数据
watchEffect(() => {
  loadData();
});
// 改变 current 题号后，会自动更新当前题目和答案
watchEffect(() => {
  currentQuestion.value = questionContent.value[currentNumber.value - 1];
  currentAnswer.value = historyAnswers[currentNumber.value - 1];
  console.log("questionContent", questionContent.value);
  console.log("currentQuestion", currentQuestion.value);
  console.log("options", options.value);
});
</script>

<style scoped></style>
