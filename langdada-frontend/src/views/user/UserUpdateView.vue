<template>
  <div id="userUpdate">
    <a-form
      :model="updateForm"
      :style="{ width: '600px' }"
      @submit="handleSubmit"
    >
      <a-form-item field="userName" label="用户名">
        <a-input v-model="updateForm.userName" placeholder="请输入用户名" />
      </a-form-item>
      <a-form-item field="userAvatar" label="头像">
        <a-input v-model="updateForm.userAvatar" placeholder="请上传头像" />
      </a-form-item>
      <a-form-item field="userProfile" label="个人简介">
        <a-input
          v-model="updateForm.userProfile"
          placeholder="请输入个人简介"
        />
      </a-form-item>
      <a-form-item field="userRole" label="角色" disabled>
        <a-input v-model="updateForm.userRole" />
      </a-form-item>
      <a-form-item field="userPassword" label="新密码">
        <a-input-password
          v-model="updateForm.userPassword"
          placeholder="请输入新密码"
        />
      </a-form-item>
      <a-form-item field="checkPassword" label="再次输入新密码">
        <a-input-password
          v-model="updateForm.checkPassword"
          placeholder="请再次输入新密码"
        />
      </a-form-item>
      <a-form-item>
        <a-button html-type="submit">修改</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
<script setup lang="ts">
import { reactive } from "vue";
import {
  getUserVoByIdUsingGet,
  updateMyUserUsingPost,
} from "@/api/userController";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import { useLoginUserStore } from "@/store/userStore";
import UserUpdateMyRequest = API.UserUpdateMyRequest;

const router = useRouter();
// 全局用户存储store
const userStore = useLoginUserStore();

const updateForm: UserUpdateMyRequest = reactive({
  id: null,
  userName: null,
  userAvatar: null,
  userProfile: null,
  userRole: null,
  // ...userStore.loginUser,
  userPassword: null,
  checkPassword: null,
});
// 从用户态中获取当前用户信息
Object.assign(updateForm, userStore.loginUser);

const handleSubmit = async () => {
  if (updateForm.userPassword !== updateForm.checkPassword) {
    message.error("两次密码不一致");
    return;
  }
  await updateMyUserUsingPost(updateForm).then((res) => {
    if (res.data.code === 0 && res.data.data) {
      message.success("修改成功");
      // 刷新存储的用户信息
      userStore.fetchLoginUser().then(() => {
        Object.assign(updateForm, userStore.loginUser);
      });
      // window.location.href = "/";
      // router.push("/");
    } else {
      message.error("修改失败" + res.data.message);
    }
  });
};
</script>
<style scoped>
#userUpdate {
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
