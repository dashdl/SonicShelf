<!--
标签选择器组件

组件功能：
1. 支持多级分类选择
2. 支持多选标签
3. 支持显示已选中标签
4. 支持自定义样式

Props:
- categories: 分类列表
- selectedTagIds: 已选中的标签ID数组
- placeholder: 占位符文本
- disabled: 是否禁用

Events:
- update:selectedTagIds: 选中标签ID变化时触发
- update:selectedTags: 选中标签对象变化时触发

使用示例：
<CategorySelector
  v-model:selectedTagIds="selectedTagIds"
  :categories="categories"
  placeholder="选择分类标签"
  @update:selectedTags="handleSelectedTagsChange"
/>
-->

<template>
  <div class="category-selector-container">
    <div class="category-selector" @click="toggleSelector" :class="{ active: showSelector, disabled: disabled }">
      <div class="selected-tags-preview" v-if="selectedTags.length > 0">
        <div class="selected-tag" v-for="item in selectedTags.slice(0, 3)" :key="item.id">
          {{ item.name }}
          <el-icon @click.stop="removeTag(item.id)">
            <Close/>
          </el-icon>
        </div>
        <span v-if="selectedTags.length > 3" class="more-tags-count">+{{ selectedTags.length - 3 }}</span>
      </div>
      <span v-else class="placeholder-text">{{ placeholder }}</span>
      <el-icon class="dropdown-icon">
        <ArrowDown v-if="!showSelector"/>
        <ArrowUp v-if="showSelector"/>
      </el-icon>
    </div>
    
    <div v-if="showSelector" class="categories-panel">
      <div v-if="loading" class="loading-state">
        <el-icon class="loading-icon">
          <Loading/>
        </el-icon>
        <span>加载中...</span>
      </div>
      
      <div v-else>
        <div class="categories-title">
          <div 
            class="title-content"
            v-for="item in internalCategories"
            :key="item.id"
            @click="changeCategory(item.id)"
            :class="{ active: item.id === currentCategoryId }"
          >
            <span :class="{ 'bold-text': item.id === currentCategoryId }">{{ item.name }}</span>
            <div v-if="item.id === currentCategoryId" class="select-underline"></div>
          </div>
        </div>
        
        <div class="categories-content">
          <div class="tags-grid-list">
            <div 
              @click="handleTagSelect(item.id)"
              v-for="item in currentTags"
              :key="item.id"
              :class="['tag-item', { 'tag-selected': selectedTagIds.includes(item.id) }]"
            >
              {{ item.name }}
            </div>
          </div>
          <div v-if="currentTags.length === 0" class="empty-tags">
            <el-icon>
              <InfoFilled/>
            </el-icon>
            <span>暂无标签</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { Close, ArrowDown, ArrowUp, InfoFilled, Loading } from '@element-plus/icons-vue'
import request from "@/utils/request.js";

// Props定义
const props = defineProps({
  // 分类列表（可选，如果提供则使用提供的分类数据）
  categories: {
    type: Array,
    default: () => []
  },
  // 已选中的标签ID数组
  selectedTagIds: {
    type: Array,
    default: () => []
  },
  // 占位符文本
  placeholder: {
    type: String,
    default: '选择分类标签'
  },
  // 是否禁用
  disabled: {
    type: Boolean,
    default: false
  }
})

// Emits定义
const emit = defineEmits([
  'update:selectedTagIds',
  'update:selectedTags'
])

// 组件内部状态
const showSelector = ref(false)
const currentCategoryId = ref(null)
const currentTags = ref([])
const internalCategories = ref([])
const loading = ref(false)

// 计算属性：选中的标签对象数组
const selectedTags = computed(() => {
  const allTags = internalCategories.value.flatMap(category => category.children || [])
  // 确保类型一致性：将标签ID转换为字符串进行比较
  return allTags.filter(tag => props.selectedTagIds.map(id => String(id)).includes(String(tag.id)))
})

// 加载根级分类数据
const loadCategories = async () => {
  if (props.categories && props.categories.length > 0) {
    // 如果父组件提供了分类数据，直接使用
    internalCategories.value = props.categories
    return
  }
  
  loading.value = true
  try {
    // 加载根级分类（parentId为null的分类）
    const response = await request.get('categories/getAll')
    
    if (response.code === '200') {
      const allCategories = response.data || []
      
      // 筛选出根级分类（parentId为null的分类）
      internalCategories.value = allCategories.filter(category => 
        category.parentId === null || category.parentId === 0
      )
      
      // 为每个根级分类添加空的children数组
      internalCategories.value.forEach(category => {
        category.children = []
      })
      
      // 设置默认当前分类ID并加载第一个分类的子分类
      if (internalCategories.value.length > 0 && !currentCategoryId.value) {
        currentCategoryId.value = internalCategories.value[0].id
        await loadTagsForCategory(currentCategoryId.value)
      }
    }
  } catch (error) {
    internalCategories.value = []
  } finally {
    loading.value = false
  }
}

// 加载指定分类的子分类
const loadTagsForCategory = async (categoryId) => {
  try {
    const response = await request.get('categories/getAll', { params: { parentId: categoryId } })
    
    if (response.code === '200') {
      const category = internalCategories.value.find(item => item.id === categoryId)
      if (category) {
        category.children = response.data || []
        
        // 如果当前分类就是刚加载子分类的分类，更新当前标签
        if (currentCategoryId.value === categoryId) {
          loadCurrentCategoryTags()
        }
      }
    }
  } catch (error) {
    // 静默处理错误
  }
}

// 监听分类列表变化，初始化当前分类和标签
watch(() => props.categories, (newCategories) => {
  if (newCategories && newCategories.length > 0) {
    internalCategories.value = newCategories
    if (internalCategories.value.length > 0 && !currentCategoryId.value) {
      currentCategoryId.value = internalCategories.value[0].id
      loadCurrentCategoryTags()
    }
  }
}, { immediate: true })

// 监听选中的标签ID变化，确保UI正确显示已选中状态
watch(() => props.selectedTagIds, () => {
  // 当选中标签ID变化时，不需要额外处理，因为selectedTags计算属性会自动更新
  // 这里主要是为了触发UI重新渲染
}, { immediate: true })

// 加载当前分类的标签
const loadCurrentCategoryTags = () => {
  if (!currentCategoryId.value) {
    return
  }
  
  const category = internalCategories.value.find(item => item.id === currentCategoryId.value)
  
  if (category && category.children) {
    currentTags.value = category.children
  } else {
    currentTags.value = []
  }
}

// 监听当前分类变化，加载对应的标签
watch(currentCategoryId, (newCategoryId) => {
  if (newCategoryId) {
    loadCurrentCategoryTags()
  }
})

// 组件挂载时初始化
onMounted(() => {
  loadCategories()
})

// 切换选择器显示/隐藏
const toggleSelector = () => {
  if (!props.disabled) {
    showSelector.value = !showSelector.value
  }
}

// 切换分类
const changeCategory = async (categoryId) => {
  currentCategoryId.value = categoryId
  
  // 检查当前分类是否已经有标签数据，如果没有则加载
  const category = internalCategories.value.find(item => item.id === categoryId)
  if (category && (!category.children || category.children.length === 0)) {
    await loadTagsForCategory(categoryId)
  }
}

// 选择/取消选择标签
const handleTagSelect = (tagId) => {
  if (props.disabled) return
  
  // 确保类型一致性：将标签ID转换为字符串进行比较
  const stringTagId = String(tagId)
  const newSelectedTagIds = [...props.selectedTagIds].map(id => String(id))
  const index = newSelectedTagIds.indexOf(stringTagId)
  const tag = currentTags.value.find(item => String(item.id) === stringTagId)
  
  if (index > -1) {
    // 取消选择
    newSelectedTagIds.splice(index, 1)
  } else {
    // 选择
    newSelectedTagIds.push(stringTagId)
  }
  
  // 触发更新事件
  emit('update:selectedTagIds', newSelectedTagIds)
  
  // 计算选中的标签对象并触发事件
  const selectedTagObjects = selectedTags.value.filter(tag => newSelectedTagIds.includes(String(tag.id)))
  emit('update:selectedTags', selectedTagObjects)
}

// 点击外部关闭选择器
const handleClickOutside = (event) => {
  if (showSelector.value) {
    // 检查点击是否发生在选择器内部
    const selectorElement = document.querySelector('.category-selector-container')
    if (selectorElement && !selectorElement.contains(event.target)) {
      showSelector.value = false
    }
  }
}

// 移除标签
const removeTag = (tagId) => {
  if (props.disabled) return
  
  // 确保类型一致性：将标签ID转换为字符串进行比较
  const stringTagId = String(tagId)
  const newSelectedTagIds = props.selectedTagIds.filter(id => String(id) !== stringTagId)
  emit('update:selectedTagIds', newSelectedTagIds)
  
  // 计算选中的标签对象并触发事件
  const selectedTagObjects = selectedTags.value.filter(tag => newSelectedTagIds.includes(String(tag.id)))
  emit('update:selectedTags', selectedTagObjects)
}

// 暴露方法给父组件
const reset = () => {
  showSelector.value = false
  currentCategoryId.value = props.categories.length > 0 ? props.categories[0].id : null
  if (currentCategoryId.value) {
    loadTagsForCategory(currentCategoryId.value)
  }
}

// 定义暴露的方法
defineExpose({
  reset
})
</script>

<style scoped>
.category-selector-container {
  position: relative;
  min-width: 280px;
  max-width: 320px;
}

.category-selector {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 10px;
  height: 30px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  background: #fff;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.category-selector:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.category-selector.disabled {
  background-color: #f5f7fa;
  cursor: not-allowed;
  opacity: 0.6;
}

.category-selector.active .dropdown-icon {
  transform: rotate(180deg);
}

.selected-tags-preview {
  display: flex;
  flex-wrap: wrap;
  gap: 3px;
  flex: 1;
  overflow: hidden;
  margin-right: 6px;
  align-items: center;
}

.selected-tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 6px;
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: #fff;
  border-radius: 3px;
  font-size: 10px;
  font-weight: 500;
  white-space: nowrap;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  margin: 1px;
}

.selected-tag .el-icon {
  margin-left: 4px;
  cursor: pointer;
  font-size: 10px;
  transition: all 0.2s;
}

.selected-tag .el-icon:hover {
  color: #ffd700;
  transform: scale(1.1);
}

.placeholder-text {
  color: #909399;
  font-size: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dropdown-icon {
  color: #c0c4cc;
  transition: transform 0.3s;
}

.more-tags-count {
  display: inline-flex;
  align-items: center;
  padding: 2px 6px;
  background: #f0f2f5;
  color: #909399;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;
  margin-left: 4px;
}

.categories-panel {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  margin-top: 4px;
  padding: 16px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
  z-index: 100;
  min-width: 320px;
  max-width: 400px;
  backdrop-filter: blur(10px);
}

.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #909399;
  font-size: 14px;
  gap: 8px;
}

.loading-icon {
  animation: spin 1s linear infinite;
  font-size: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.categories-title {
  display: flex;
  gap: 4px;
  margin-bottom: 16px;
  padding: 8px 12px;
  border-bottom: 1px solid #f0f2f5;
  background: linear-gradient(135deg, #fafbfc 0%, #f5f7fa 100%);
  flex-shrink: 0;
}

.title-content {
  flex: 1;
  text-align: center;
  position: relative;
  cursor: pointer;
  padding: 10px 8px;
  border-radius: 6px;
  transition: all 0.3s;
  min-height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.title-content:hover {
  background: rgba(64, 158, 255, 0.08);
}

.title-content.active {
  background: rgba(64, 158, 255, 0.12);
}

.title-content span {
  font-size: 13px;
  color: #606266;
  transition: all 0.3s;
  font-weight: 500;
}

.bold-text {
  color: #409eff;
  font-weight: 600;
}

.select-underline {
  position: absolute;
  bottom: 2px;
  left: 25%;
  right: 25%;
  height: 2px;
  background: #409eff;
  border-radius: 1px;
}

.categories-content {
  max-height: 280px;
  overflow-y: auto;
}

.tags-grid-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(70px, 1fr));
  gap: 6px;
  padding: 6px;
}

.tag-item {
  padding: 6px 4px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background: #f8f9fa;
  cursor: pointer;
  text-align: center;
  font-size: 11px;
  color: #606266;
  transition: all 0.2s;
  position: relative;
  overflow: hidden;
  min-height: 24px;
  line-height: 1.2;
}

.tag-item:hover {
  border-color: #409eff;
  background: #f0f7ff;
  color: #409eff;
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(64, 158, 255, 0.15);
}

.tag-item.tag-selected {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: #fff;
  border-color: #409eff;
  box-shadow: 0 2px 6px rgba(64, 158, 255, 0.25);
  font-weight: 500;
}

.tag-item.tag-selected::after {
  content: '✓';
  position: absolute;
  top: 1px;
  right: 2px;
  font-size: 9px;
  font-weight: bold;
}

.empty-tags {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  color: #909399;
  font-size: 13px;
  gap: 6px;
}

.empty-tags .el-icon {
  font-size: 16px;
}

/* 滚动条样式 */
.categories-content::-webkit-scrollbar {
  width: 6px;
}

.categories-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.categories-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.categories-content::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>