<template>
  <Editor :value="value" :plugins="plugins" @change="handleChange" />
</template>
<script setup lang="ts">
import gfm from "@bytemd/plugin-gfm";
import { ref, withDefaults, defineProps } from "vue";
import { Editor } from "@bytemd/vue-next";
import highlight from "@bytemd/plugin-highlight";
import "bytemd/dist/index.css";

const value = ref("");
const plugins = [
  gfm(),
  highlight(),
  // Add more plugins here
];

interface Props {
  value: string;
  mode?: string;
  handleChange: (v: string) => void;
}

const props = withDefaults(defineProps<Props>(), {
  value: () => "",
  mode: () => "split",
  handleChange: (v: string) => {
    console.log(v);
  },
});

const handleChange = (v: string) => {
  value.value = v;
};
</script>
<style scoped></style>
