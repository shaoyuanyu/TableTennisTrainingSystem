<template>
    <BaseButton v-bind="{ ...$attrs, ...$props, circle: square ? false : circle }" class="icon-button">
        <!-- 优先转发具名图标槽 -->
        <template v-if="$slots['icon-left']" #icon-left>
            <slot name="icon-left" />
        </template>
        <template v-else #icon-left>
            <!-- 兼容默认插槽直接传图标的写法 -->
            <slot />
        </template>

        <template v-if="$slots['icon-right']" #icon-right>
            <slot name="icon-right" />
        </template>

        <!-- IconButton 不渲染按钮文本 -->
        <template #default />
    </BaseButton>

</template>

<script>
import BaseButton from './BaseButton.vue'
export default {
    name: 'IconButton',
    components: { BaseButton },
    inheritAttrs: false,
    props: {
        ...BaseButton.props,
        circle: { type: Boolean, default: true },
        square: { type: Boolean, default: false },
    },
}
</script>

<style scoped>
.icon-button :deep(.btn-text) {
    display: none;
}

/* 纯图标按钮中，去掉左右图标的外边距，确保视觉居中 */
.icon-button :deep(.btn-icon-left),
.icon-button :deep(.btn-icon-right) {
    margin: 0;
}

/* 提升图标尺寸与可读性（遵循按钮尺寸） */
.icon-button :deep(.btn-icon) {
    font-size: 16px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
}
</style>
