import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import { createPinia } from "pinia";
import piniaPluginPersist from "pinia-plugin-persist";
// 额外引入图标库
import ArcoVueIcon from "@arco-design/web-vue/es/icon";
import "@/access";
import ArcoVue from "@arco-design/web-vue";
import "@arco-design/web-vue/dist/arco.css";

const pinia = createPinia();
pinia.use(piniaPluginPersist);
createApp(App)
  .use(ArcoVue)
  .use(ArcoVueIcon)
  .use(pinia)
  .use(router)
  .mount("#app");
