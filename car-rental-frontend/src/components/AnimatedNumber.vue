<template>
  <span>{{ display }}</span>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const props = defineProps({
  target: { type: Number, default: 0 },
  duration: { type: Number, default: 1200 },
})

const display = ref(0)

onMounted(() => {
  if (props.target === 0) { display.value = 0; return }
  const steps = 40
  const increment = props.target / steps
  let current = 0
  const timer = setInterval(() => {
    current += increment
    if (current >= props.target) {
      display.value = props.target
      clearInterval(timer)
    } else {
      display.value = Math.floor(current)
    }
  }, props.duration / steps)
})
</script>
