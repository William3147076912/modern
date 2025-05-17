<template>
  <div>
    <svg-icon icon-class="github" @click="goto" />
  </div>
</template>

<script setup>
const url = ref('https://github.com/William3147076912/modern');

function goto() {
  window.open(url.value)
}
</script>
