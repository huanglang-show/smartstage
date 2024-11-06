<template>
  <div id="userLayout">
    <a-layout style="height: 100vh">
      <a-layout-header class="header">
        <div class="titleBar">
          <img class="logo" src="../../public/logo.jpg" alt="logo" />
          <div class="title">答题平台</div>
        </div>
      </a-layout-header>
      <a-layout-content class="content">
        <router-view></router-view>
      </a-layout-content>
      <a-layout-footer class="footer">by HuangLang</a-layout-footer>
    </a-layout>
  </div>
</template>

<script setup lang="ts"></script>
<style scoped>
#userLayout {
  text-align: center;
  background: url("https://gw.alipayobjects.com/zos/rmsportal/FfdJeJRQWjEeGTpqgBKj.png")
    0% 0% / 100% 100%;
}

#userLayout .header {
  margin-top: 16px;
  font-size: 21px;
}

#userLayout .logo {
  width: 48px;
  height: 48px;
}

#userLayout .content {
  max-width: 1200px;
  width: 100%;
  box-sizing: border-box;
  margin: 0 auto 28px;
  padding: 20px;
  background: linear-gradient(to right, #fefefe, #fff);
}

#userLayout .footer {
  padding: 16px;
  text-align: center;
  background: #efefef;
}
</style>
