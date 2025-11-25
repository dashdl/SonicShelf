package com.zhongxin.sonicshelf.mapper;

import com.zhongxin.sonicshelf.dto.request.DynamicRequest;
import com.zhongxin.sonicshelf.dto.response.DynamicResponse;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DynamicMapper {

    @Update("update dynamics set like_count = #{likeCount} where id = #{targetId}")
    void updateLikeCount(Long targetId, Integer likeCount);

    List<DynamicResponse> selectAllByUserId(Long[] ids, Long currentUserId);

    @Select("select image_url from dynamic_images where dynamic_id = #{id} order by sort_order")
    String[] selectImagesByDynamicId(Long id);

    @Update("update dynamics set comment_count = #{count} where id = #{id}")
    void updateCommentCount(Integer count, Long id);

    @Insert("insert into dynamics (user_id, content, type, target_id) values (#{currentUserId},#{dynamic.content},#{dynamic.type},#{dynamic.targetId})")
    @Options(useGeneratedKeys = true, keyProperty = "dynamic.id", keyColumn = "id")
    void insertDynamic(@Param("dynamic") DynamicRequest dynamic, @Param("currentUserId") Long currentUserId);

    @Insert("insert into dynamic_images (dynamic_id, image_url,sort_order) VALUES(#{id},#{url},#{sort}) ")
    void insertDynamicImage(String url, Long id, int sort);

    List<DynamicResponse> selectByUserId(Long userId);

    @Delete("delete from dynamics where id = #{id}")
    void deleteDynamicById(Long id);

    @Delete("delete from dynamic_images where dynamic_id = #{id}")
    void deleteDynamicImageById(Long id);

    @Select("select user_id from dynamics where id = #{id}")
    Long getUserIdById(Long id);
}
