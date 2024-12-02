import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import { createPinia } from "pinia";
import piniaPluginPersist from "pinia-plugin-persist";
import "@/access";
import ArcoVue from "@arco-design/web-vue";
import "@arco-design/web-vue/dist/arco.css";

const pinia = createPinia();
pinia.use(piniaPluginPersist);
createApp(App).use(ArcoVue).use(pinia).use(router).mount("#app");
