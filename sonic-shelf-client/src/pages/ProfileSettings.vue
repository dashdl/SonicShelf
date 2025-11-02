<script setup>
import {onMounted, reactive, watch, ref, onUnmounted} from 'vue';
import request from '@/utils/request.js';
import {ElMessage} from 'element-plus';
import {useUserStore} from "@/store/userStore.js";

const userStore = useUserStore();

const formData = reactive({
  nickname: '',
  bio: '',
  gender: 1, // 1:男, 2:女
  birthYear: '',
  birthMonth: '',
  birthDay: '',
  province: '',
  city: '',
  avatar: ''
});

const dateOptions = reactive({
  years: [],
  months: [],
  days: []
});

const regionOptions = reactive({
  provinces: [],
  cities: []
});

const avatarPreview = ref('');
const fileInput = ref(null);
let selectedFile = null;

const initYears = () => {
  const currentYear = new Date().getFullYear();
  for (let year = currentYear; year >= 1950; year--) {
    dateOptions.years.push(year);
  }
};
const initMonths = () => {
  for (let month = 1; month <= 12; month++) {

    if (month < 10) {
      dateOptions.months.push("0" + month);
    } else {
      dateOptions.months.push(month);
    }


  }
};
const updateDays = () => {
  if (!formData.birthYear || !formData.birthMonth) {
    dateOptions.days = [];
    return;
  }

  const year = parseInt(formData.birthYear);
  const month = parseInt(formData.birthMonth);

  const daysInMonth = new Date(year, month, 0).getDate();

  dateOptions.days = [];
  for (let day = 1; day <= daysInMonth; day++) {
    if (day < 10) {
      dateOptions.days.push("0" + day);
    } else {
      dateOptions.days.push(day);
    }
  }

  // 如果之前选择的天数超过了当月最大天数，清空天数选择
  if (formData.birthDay > daysInMonth) {
    formData.birthDay = '';
  }
};

const initProvinces = async () => {
  try {
    const response = await fetch('https://raw.githubusercontent.com/modood/Administrative-divisions-of-China/master/dist/provinces.json');

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();

    // 提取省份数据
    if (Array.isArray(data)) {
      regionOptions.provinces = data.map(item => ({
        value: item.code,
        label: item.name
      }));
    } else {
      // 如果数据结构不符合预期，使用备用API
      await fetchProvincesFromAlternativeAPI();
    }
  } catch (error) {
    console.error('获取省份数据失败:', error);
    // 尝试使用备用API
    await fetchProvincesFromAlternativeAPI();
  }
};

const updateCities = async () => {
  if (!formData.province) {
    regionOptions.cities = [];
    formData.city = '';
    return;
  }

  // 保存当前城市值，以便在更新后检查是否有效
  const currentCity = formData.city;

  try {
    // 使用GitHub Pages托管的城市数据
    const response = await fetch('https://raw.githubusercontent.com/modood/Administrative-divisions-of-China/master/dist/cities.json');

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();

    // 过滤出对应省份的城市
    if (Array.isArray(data)) {
      const filteredCities = data.filter(city => city.code.startsWith(formData.province));

      if (filteredCities.length > 0) {
        // 提取城市数据
        regionOptions.cities = filteredCities.map(item => ({
          value: item.code,
          label: item.name
        }));
      } else {
        // 如果找不到对应省份的城市，使用备用API
        await fetchCitiesFromAlternativeAPI();
      }
    } else {
      await fetchCitiesFromAlternativeAPI();
    }
  } catch (error) {
    console.error('获取城市数据失败:', error);
    // 尝试使用备用API
    await fetchCitiesFromAlternativeAPI();
  }

  // 检查保存的城市值是否在新的城市列表中，如果是则保留，否则清空
  const isCityValid = regionOptions.cities.some(city => city.value === currentCity);
  if (!isCityValid) {
    formData.city = '';
  }
  // 注意：如果城市值有效，不清空它，保留用户原有的选择
};

const fetchProvincesFromAlternativeAPI = async () => {
  try {
    const response = await fetch('https://cdn.jsdelivr.net/npm/china-province-city-district-data@1.2.3/data.js');

    if (!response.ok) {
      throw new Error(`备用API请求失败`);
    }

    // 处理响应数据
    const text = await response.text();
    // 提取JSON部分（移除可能的赋值语句）
    const jsonMatch = text.match(/\{[^;]+\}/);

    if (jsonMatch && jsonMatch[0]) {
      const data = JSON.parse(jsonMatch[0]);
      // 提取省份数据
      if (data) {
        regionOptions.provinces = Object.entries(data).map(([code, name]) => ({
          value: code,
          label: name
        }));
      }
    }
  } catch (fallbackError) {
    console.error('备用API获取省份数据失败:', fallbackError);
    regionOptions.provinces = [
      {value: '110000', label: '北京市'},
      {value: '310000', label: '上海市'},
      {value: '440000', label: '广东省'}
    ];
  }
};

const fetchCitiesFromAlternativeAPI = async () => {
  try {
    if (!formData.province) {
      regionOptions.cities = [];
      return;
    }

    const province = regionOptions.provinces.find(p => p.value === formData.province);
    if (province) {
      regionOptions.cities = [
        {value: `${formData.province}01`, label: `${province.label}市`}
      ];
    } else {
      regionOptions.cities = [];
    }
  } catch (error) {
    console.error('备用API获取城市数据失败:', error);
    regionOptions.cities = [];
  }
};

// 添加文件上传相关方法
const triggerFileUpload = () => {
  fileInput.value.click();
};

const handleFileSelect = (event) => {
  const file = event.target.files[0];
  if (file) {
    // 验证文件类型和大小
    const validTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
    const maxSize = 2 * 1024 * 1024; // 2MB

    if (!validTypes.includes(file.type)) {
      ElMessage.warning('只支持JPG、PNG、GIF格式的图片');
      return;
    }

    if (file.size > maxSize) {
      ElMessage.warning('图片大小不能超过2MB');
      return;
    }

    // 保存文件引用
    selectedFile = file;

    // 生成预览URL
    if (avatarPreview.value) {
      URL.revokeObjectURL(avatarPreview.value); // 清理之前的预览以避免内存泄漏
    }
    avatarPreview.value = URL.createObjectURL(file);
  }
};

watch(() => [formData.birthYear, formData.birthMonth], () => {
  updateDays();
});

watch(() => formData.province, async () => {
  await updateCities();
});

onMounted(async () => {
  // 初始化日期选项
  initYears();
  initMonths();

  // 初始化地区选项（异步）
  await initProvinces();

  // 加载用户信息
    const userData = userStore.userInfo || {};
    formData.nickname = userData.nickname || '';
    formData.bio = userData.bio || '';
    formData.gender = userData.gender || 1;

    // 解析生日
    if (userData.birthday) {
      const [year, month, day] = userData.birthday.split('-');
      formData.birthYear = year;
      formData.birthMonth = month;
      formData.birthDay = day;
    }

    // 地区解析逻辑优化
    if (userData.location) {
      try {
        const locationParts = userData.location.split('-');
        const provinceName = locationParts[0];
        const cityName = locationParts[1];
        
        // 查找对应的省份编码
        const province = regionOptions.provinces.find(p => p.label === provinceName);
        if (province) {
          formData.province = province.value;
          
          // 确保城市列表已加载
          await updateCities();
          
          // 尝试精确匹配城市
          let city = regionOptions.cities.find(c => c.label === cityName);
          
          // 如果精确匹配失败，尝试模糊匹配
          if (!city && cityName) {
            city = regionOptions.cities.find(c => c.label.includes(cityName) || cityName.includes(c.label));
          }
          
          // 使用setTimeout确保DOM更新后再设置城市值，解决响应式绑定问题
          setTimeout(() => {
            if (city) {
              formData.city = city.value;
            } else if (regionOptions.cities.length > 0) {
              formData.city = regionOptions.cities[0].value;
            }
          }, 0);
        }
      } catch (error) {
        console.error('解析地区信息时出错:', error);
      }
    }

    formData.avatar = userData.avatar || '';

    const avatarPath = formData.avatar;
    const baseUrl = 'http://localhost:8080';
    const imgElement = document.getElementById('userAvatar');
    if (imgElement) {
      imgElement.src = avatarPath ? baseUrl + avatarPath : '/images/default/avatar.jpg';
    }
});

const saveProfile = async () => {
  if (!formData.nickname.trim()) {
    ElMessage.warning('昵称不能为空');
    return;
  }

  // 如果有选择新头像，先上传头像
  if (selectedFile) {
    try {
      const formDataForUpload = new FormData();
      formDataForUpload.append('file', selectedFile);

      console.log('上传文件信息:', selectedFile);
      console.log('FormData内容:', formDataForUpload.getAll('file'));
      console.log('API请求路径:', '/upload/avatar');

      // 上传头像
      const uploadResponse = await request.post('/upload/avatar', formDataForUpload);
      console.log('上传响应:', uploadResponse);

      if (uploadResponse.code === '200') {
        // 更新头像路径到formData
        formData.avatar = uploadResponse.data.avatarPath;
      } else {
        ElMessage.error(uploadResponse.message || '头像上传失败');
        return;
      }
    } catch (error) {
      console.error('头像上传错误详情:', error);
      console.error('错误状态:', error.response?.status);
      console.error('错误数据:', error.response?.data);
      ElMessage.error('头像上传失败');
      return;
    }
  }

  // 修改后的保存个人信息逻辑
  const submitData = {
    ...formData,
    birthday: formData.birthYear && formData.birthMonth && formData.birthDay
        ? `${formData.birthYear}-${formData.birthMonth}-${formData.birthDay}`
        : null,
    location: formData.province && formData.city
        ? `${regionOptions.provinces.find(p => p.value === formData.province)?.label || formData.province}-${regionOptions.cities.find(c => c.value === formData.city)?.label || formData.city}`
        : null
  };

  request.put('/users/profile', submitData).then(async res => {
    if (res.code === '200') {
      ElMessage.success('个人信息保存成功');
      try {
        await userStore.restoreUserState();
      } catch (error) {
        ElMessage.error("用户状态加载失败");
      }
    } else {
      ElMessage.error(res.message || '个人信息保存失败');
    }
  }).catch(err => {
    ElMessage.error('个人信息保存失败');
  });
};

const cancelEdit = () => {
  ElMessage.info('已取消编辑');
};

// 组件卸载时清理资源
onUnmounted(() => {
  if (avatarPreview.value) {
    URL.revokeObjectURL(avatarPreview.value);
  }
});

</script>

<template>
  <div class="profile-container">
    <div class="profile" style=" margin-right: 45px;">
      <div class="title">
        编辑个人信息
      </div>
      <div class="profile-item">
        <label style="margin-right: 30px">昵称：</label>
        <input v-model="formData.nickname" placeholder="请输入昵称">
      </div>
      <div class="profile-item">
        <label style="margin-right: 30px;">简介：</label>
        <textarea v-model="formData.bio" style="height: 150px;outline: none;resize: none;"
                  placeholder="请输入个人简介"></textarea>
      </div>
      <div class="profile-item">
        <label style="margin-right: 30px">性别：</label>
        <input id="radio1" type="radio" name="gender" value="1" v-model="formData.gender" style="margin-right:15px;">
        <label for="radio1" style="margin-right: 30px;">男</label>
        <input id="radio0" type="radio" name="gender" value="2" v-model="formData.gender" style="margin-right:15px;">
        <label for="radio0">女</label>
      </div>
      <div class="profile-item">
        <label style="margin-right: 30px">生日：</label>
        <select v-model="formData.birthYear" style="width: 133px;">
          <option v-for="year in dateOptions.years" :key="year" :value="year">{{ year }}</option>
        </select>
        <select v-model="formData.birthMonth" style="width: 133px;">
          <option v-for="month in dateOptions.months" :key="month" :value="month">{{ month }}</option>
        </select>
        <select v-model="formData.birthDay" style="width: 133px;">
          <option v-for="day in dateOptions.days" :key="day" :value="day">{{ day }}</option>
        </select>
      </div>
      <div class="profile-item">
        <label style="margin-right: 30px">地区：</label>
        <select v-model="formData.province" style="width: 133px;">
          <option value="">请选择省份</option>
          <option v-for="province in regionOptions.provinces" :key="province.value" :value="province.value">
            {{ province.label }}
          </option>
        </select>
        <select v-model="formData.city" style="width: 133px;">
          <option value="">请选择城市</option>
          <option v-for="city in regionOptions.cities" :key="city.value" :value="city.value">{{ city.label }}</option>
        </select>
      </div>
      <div class="profile-item" style="padding-left: 74px;margin-top: 36px">
        <div class="save-button" @click="saveProfile">保存</div>
        <div class="cancel-button" @click="cancelEdit">取消</div>
      </div>
    </div>
    <div class="avatar">
      <img
          id="userAvatar"
          :src="avatarPreview || (formData.avatar ? 'http://localhost:8080' + formData.avatar : '/images/default/avatar.jpg')"
          style="width: 140px;height: 140px;cursor: pointer;"
          alt="点击上传头像"
          @click="triggerFileUpload"
      >
      <input type="file" ref="fileInput" @change="handleFileSelect" accept="image/*" style="display: none;">
    </div>
  </div>
</template>

<style scoped>
.profile-container {
  width: 757px;
  display: flex;
  justify-items: left;
  padding: 15px 0;
  background-color: #f7f9fc;
  user-select: none;
}

.title {
  margin-bottom: 30px;
  font-weight: bold;
  font-size: 30px;
  color: #515a6c;
}

.profile-item {
  display: flex;
  margin-bottom: 18px;
}

input, textarea {
  padding: 10px;
  height: 40px;
  width: 440px;
  border: 1px solid #e4e8ec;
  border-radius: 10px;
  background-color: #f0f3f6;
}

#radio1, #radio0 {
  height: 20px;
  width: 20px;
  border: 1px solid #e4e8ec;
  background-color: #f7f9fc;
}

select {
  appearance: none;
  -webkit-appearance: none; /* Safari 和 Chrome */
  -moz-appearance: none; /* Firefox */
  padding: 0 10px;
  width: 133px;
  height: 30px;
  border: 1px solid #e4e8ec;
  border-radius: 30px;
  margin-right: 20px;
  background-color: #f0f3f6;
  outline: none;
}

option {
  padding: 8px 12px;
  border: 1px solid #e4e8ec;
  border-radius: 10px;
}

.save-button, .cancel-button {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 30px;
  height: 50px;
  width: 120px;
  border-radius: 25px;
  font-size: 20px;
  user-select: none;
  cursor: pointer;
}

.save-button {
  color: white;
  background-color: #fc3d49;
}

.cancel-button {
  border: 1px solid #e4e8ec;
  background-color: #f7f9fc;
}

.avatar img {
  margin-top: 75px;
  width: 140px;
  height: 140px;
  border-radius: 70px;
  cursor: pointer;
}

</style>