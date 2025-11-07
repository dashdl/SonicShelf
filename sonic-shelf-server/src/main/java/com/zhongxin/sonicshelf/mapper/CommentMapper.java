package com.zhongxin.sonicshelf.mapper;

import com.zhongxin.sonicshelf.dto.response.CommentResponse;
import com.zhongxin.sonicshelf.entity.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {

    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "user_id", property = "userId"),
            @Result(column = "parent_Id", property = "parentId"),
            @Result(column = "nickname", property = "nickname"),
            @Result(column = "avatar", property = "avatar"),
            @Result(column = "content", property = "content"),
            @Result(column = "created_at", property = "createdAt"),
            @Result(column = "like_count", property = "likeCount"),
            @Result(column = "target_type", property = "targetType"),
            @Result(column = "target_id", property = "targetId"),
    })

    @Select("    select c.id," +
            "    u.id as user_id," +
            "    u.nickname," +
            "    u.avatar," +
            "    c.content," +
            "    c.created_at," +
            "    c.parent_id," +
            "    c.like_count" +
            "    from comments c" +
            "    left join users u on c.user_id = u.id" +
            "    where target_id = #{targetId}" +
            "    and target_type = #{targetType}" +
            "    order by c.created_at desc")
    List<CommentResponse> findCommentsByTargetTypeAndTargetId(String targetType, Long targetId);

    @Select("    select c.id," +
            "    u.id as user_id," +
            "    u.nickname," +
            "    u.avatar," +
            "    c.content," +
            "    c.created_at," +
            "    c.parent_id," +
            "    c.like_count" +
            "    from comments c" +
            "    left join users u on c.user_id = u.id" +
            "    where c.id = #{parentId}" +
            "    order by c.created_at desc")
    CommentResponse findCommentById(Long parentId);

    @Insert("insert into comments " +
            "(user_id, target_type, target_id, content, parent_id)" +
            " values " +
            "(#{userId},#{targetType},#{targetId},#{content},#{parentId})")
    void addComment(Comment comment);

    @Select("select user_id,target_id,target_type,id from comments where id = #{commentId}")
    Comment selectByCommentId(Long commentId);

    @Delete("delete from comments where id=#{commentId}")
    void deleteById(Long commentId);
}
