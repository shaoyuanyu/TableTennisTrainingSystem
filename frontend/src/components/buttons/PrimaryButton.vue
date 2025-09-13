<template>
    <BaseButton color="primary" v-bind="{ ...$props, ...$attrs }">
        <slot />
    </BaseButton>
</template>

<script>
import BaseButton from './BaseButton.vue'
export default {
    name: 'PrimaryButton',
    components: { BaseButton },
    inheritAttrs: false,
    props: { ...BaseButton.props },
}
</script>
