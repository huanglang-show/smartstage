<template>
  <div id="adminUpdateUserModal">
    <a-modal
      :visible="isModalVisible"
      title="修改用户信息"
      @cancel="handleCancel"
      @before-ok="handleBeforeOk"
    >
      <a-form :model="user">
        <a-form-item field="userAccount" label="账号" disabled>
          <a-input v-model="user.userAccount" />
        </a-form-item>
        <a-form-item field="userName" label="用户名">
          <a-input v-model="user.userName" />
        </a-form-item>
        <a-form-item field="userAvatar" label="用户头像">
          <PictureUpload biz="userAvatar" :value="user.userAvatar" />
        </a-form-item>
        <a-form-item field="userProfile" label="用户简介">
          <a-input v-model="user.userProfile" />
        </a-form-item>
        <a-form-item field="userRole" label="权限">
          <a-input v-model="user.userRole" />
        </a-form-item>
        <a-form-item field="userPassword" label="用户密码">
          <a-input-password v-model="user.userPassword" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>
<script setup lang="ts">
import API from "@/api/typings";
import { withDefaults, defineProps, ref, watch } from "vue";
import { defineEmits } from "vue";
import PictureUpload from "@/components/PictureUpload.vue";

interface Props {
  isModalVisible: boolean;
  userData: API.User;
}

const props = withDefaults(defineProps<Props>(), {
  isModalVisible: false,
});

const user = ref<API.User>({ ...props.userData });
watch(
  () => props.userData,
  (newVal) => {
    user.value = { ...newVal };
  }
);
const emit = defineEmits(["cancelModal", "handleUpdateUser"]);
const handleCancel = () => {
  emit("cancelModal", false);
};

const handleBeforeOk = () => {
  emit("handleUpdateUser", user.value);
};
</script>

<style scoped></style>
