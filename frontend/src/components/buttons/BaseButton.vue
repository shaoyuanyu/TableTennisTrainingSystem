<template>
    <component :is="href ? 'a' : 'button'" :href="href || undefined" :target="href ? target : undefined"
        :rel="href ? relComputed : undefined" :type="href ? undefined : nativeType" :class="classes"
        :disabled="!href && (disabled || loading)" @click="onClick">
        <span v-if="loading" class="loading-spinner" />

        <!-- 左侧图标：优先 slot，其次使用 iconLeft class -->
        <span v-if="$slots['icon-left'] && !loading" class="btn-icon btn-icon-left">
            <slot name="icon-left" />
        </span>
        <i v-else-if="iconLeft && !loading" :class="['btn-icon', 'btn-icon-left', iconLeft]" />

        <span class="btn-text">
            <slot>{{ label }}</slot>
        </span>

        <!-- 右侧图标：优先 slot，其次使用 iconRight class -->
        <span v-if="$slots['icon-right'] && !loading" class="btn-icon btn-icon-right">
            <slot name="icon-right" />
        </span>
        <i v-else-if="iconRight && !loading" :class="['btn-icon', 'btn-icon-right', iconRight]" />
    </component>
</template>

<script>
export default {
    name: 'BaseButton',
    props: {
        label: { type: String, default: '' },
        color: {
            type: String,
            default: 'primary',
            validator: (v) =>
                ['primary', 'secondary', 'success', 'warning', 'danger', 'info'].includes(v),
        },
        variant: {
            type: String,
            default: 'solid', // solid | outline | glass
            validator: (v) => ['solid', 'outline', 'glass'].includes(v),
        },
        size: {
            type: String,
            default: 'md', // sm | md | lg
            validator: (v) => ['sm', 'md', 'lg', 'small', 'medium', 'large'].includes(v),
        },
        loading: { type: Boolean, default: false },
        disabled: { type: Boolean, default: false },
        block: { type: Boolean, default: false },
        round: { type: Boolean, default: false }, // 胶囊形
        circle: { type: Boolean, default: false }, // 圆形按钮
        square: { type: Boolean, default: false }, // 等宽高的圆角方形（适合纯图标）
        iconLeft: { type: String, default: '' },
        iconRight: { type: String, default: '' },
        nativeType: { type: String, default: 'button' },
        href: { type: String, default: '' },
        to: { type: [String, Object], default: '' }, // vue-router location
        replace: { type: Boolean, default: false },
        target: { type: String, default: '_self' }, // only for href
        rel: { type: String, default: '' }, // only for href
        extraClasses: { type: [String, Array, Object], default: '' },
    },
    emits: ['click'],
    computed: {
        relComputed() {
            // add noopener noreferrer automatically for external new tab
            if (!this.rel && this.target === '_blank') return 'noopener noreferrer'
            return this.rel || undefined
        },
        classes() {
            const sizeMap = { small: 'sm', medium: 'md', large: 'lg' }
            const normalizedSize = sizeMap[this.size] || this.size

            const classes = [
                'btn-modern',
                `btn-${this.color}`,
                this.variant === 'outline' ? 'btn-outline' : '',
                this.variant === 'glass' ? 'btn-glass' : '',
                normalizedSize === 'sm' ? 'btn-small' : '',
                normalizedSize === 'lg' ? 'btn-large' : '',
                this.block ? 'btn-block' : '',
                this.loading ? 'btn-loading' : '',
                this.round ? 'btn-round' : '',
                this.circle ? 'btn-circle' : '',
                this.square ? 'btn-square' : '',
            ].filter(Boolean)

            if (this.extraClasses) classes.push(this.extraClasses)
            return classes
        },
    },
    methods: {
        onClick(e) {
            if (this.disabled || this.loading) return
            // Router navigation support when `to` is provided
            if (this.to) {
                try {
                    if (this.replace && this.$router) {
                        this.$router.replace(this.to)
                    } else if (this.$router) {
                        this.$router.push(this.to)
                    }
                } catch (err) {
                    // swallow navigation duplicated errors
                    console.warn('[BaseButton] navigation error:', err)
                }
            }
            this.$emit('click', e)
        },
    },
}
</script>

<style scoped>
.loading-spinner {
    display: inline-block;
    width: 12px;
    height: 12px;
    border: 2px solid transparent;
    border-top: 2px solid currentColor;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-right: var(--spacing-sm);
}

@keyframes spin {
    0% {
        transform: rotate(0deg);
    }

    100% {
        transform: rotate(360deg);
    }
}

.btn-icon {
    font-size: var(--font-size-sm);
    line-height: 1;
}

.btn-icon-left {
    margin-right: var(--spacing-sm);
}

.btn-icon-right {
    margin-left: var(--spacing-sm);
}

.btn-text {
    display: inline-block;
}

.btn-block {
    width: 100%;
    display: inline-flex;
    justify-content: center;
}

.btn-loading {
    cursor: wait;
}

.btn-round {
    border-radius: 999px;
}

.btn-circle {
    border-radius: 50%;
    width: 36px;
    height: 36px;
    padding: 0;
    align-items: center;
    justify-content: center;
}

/* 尺寸适配：纯图标圆形/圆角方形按钮固定等宽高 */
.btn-small.btn-circle {
    width: 28px;
    height: 28px;
}

.btn-large.btn-circle {
    width: 44px;
    height: 44px;
}

.btn-square {
    border-radius: var(--radius-lg);
    width: 36px;
    height: 36px;
    padding: 0;
    align-items: center;
    justify-content: center;
}

.btn-small.btn-square {
    width: 28px;
    height: 28px;
}

.btn-large.btn-square {
    width: 44px;
    height: 44px;
}
</style>
