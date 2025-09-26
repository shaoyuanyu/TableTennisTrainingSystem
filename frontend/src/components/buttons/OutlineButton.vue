<template>
    <BaseButton variant="outline" v-bind="{ ...$props, ...$attrs }">
        <slot />
    </BaseButton>
</template>

<script>
import BaseButton from './BaseButton.vue'

export default {
    name: 'OutlineButton',
    components: { BaseButton },
    inheritAttrs: false,
    props: { ...BaseButton.props },
}
</script>
