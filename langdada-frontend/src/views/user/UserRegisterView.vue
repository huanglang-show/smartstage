<template>
  <div id="userRegister">
    <a-space size="large" :style="{ width: '600px' }">
      <a-form
        class="registerForm"
        :model="registerForm"
        :style="{ width: '600px' }"
        @submit="handleSubmit"
        layout="vertical"
      >
        <a-form-item
          field="账号"
          required
          rules="form.userAccount!=''"
          label="账号"
        >
          <a-input
            v-model="registerForm.userAccount"
            placeholder="请输入用户名"
          />
        </a-form-item>
        <a-form-item field="密码" label="密码" required>
          <a-input-password
            v-model="registerForm.userPassword"
            placeholder="请输入密码"
          />
        </a-form-item>
        <a-form-item field="再次输入密码" label="再次输入密码" required>
          <a-input-password
            v-model="registerForm.checkPassword"
            placeholder="请再次输入密码"
          />
        </a-form-item>
        <!--        <a-form-item field="isRead">-->
        <!--          <a-checkbox v-model="form.isRead"> I have read the manual</a-checkbox>-->
        <!--        </a-form-item>-->
        <a-form-item>
          <a-button
            html-type="loginUser"
            @click="loginUser"
            class="loginBt"
            type="primary"
            >登录
          </a-button>
          <a-button html-type="submit" class="registerBt" type="primary"
            >注册
          </a-button>
        </a-form-item>
      </a-form>
    </a-space>
  </div>
</template>
<script setup lang="ts">
import { reactive } from "vue";
import { userRegisterUsingPost } from "@/api/userController";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";

const router = useRouter();
const registerForm = reactive({
  userAccount: "",
  userPassword: "",
  checkPassword: "",
  isRead: false,
});

const handleSubmit = async () => {
  if (registerForm.userPassword !== registerForm.checkPassword) {
    message.error("两次密码不一致");
    return;
  }
  await userRegisterUsingPost(registerForm).then((res) => {
    if (res.data.code === 0 && res.data.data) {
      message.success("注册成功");
      // window.location.href = "/";
      router.push({
        path: "/user/login",
      });
    } else {
      message.error("注册失败" + res.data.message);
    }
  });
};

const loginUser = () => {
  router.push({
    path: "/user/login",
  });
};
</script>
<style scoped>
#userRegister {
  align-content: center;
  background: linear-gradient(to right, #fefefe, #fff);
}

.registerForm {
  margin: 16px auto;
  background: linear-gradient(to right, #fefefe, #fff);
}

.registerBt {
  margin: 0 auto;
}

.loginBt {
  margin: 0 auto;
}
</style>
