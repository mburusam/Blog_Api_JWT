package blog_app.service.impl;

import blog_app.entity.Comment;
import blog_app.entity.Post;
import blog_app.exceptions.BlogApiException;
import blog_app.exceptions.ResourceNotFoundException;
import blog_app.payload.CommentDto;
import blog_app.repository.CommentRepository;
import blog_app.repository.PostRepository;
import blog_app.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final ModelMapper modelMapper;
    private final PostRepository postRepository;

    public CommentServiceImpl(CommentRepository commentRepository, ModelMapper modelMapper, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.modelMapper = modelMapper;
        this.postRepository = postRepository;
    }

    @Override
    public CommentDto createComment(long postId, CommentDto commentDto) {
        Comment comment =mapToEntity(commentDto);
        //retrieve the post by id
        Post post = postRepository.findById(postId).orElseThrow(()-> new ResourceNotFoundException("Post","id",postId));
        comment.setPost(post);
        Comment newComment = commentRepository.save(comment);
        return mapToDTO(newComment);
    }

    @Override
    public List<CommentDto> getCommentsByPostId(long postId) {
        //retrieve comments by postid
         List<Comment> comments =commentRepository.findByPostId(postId);
        return comments.stream().map(comment -> mapToDTO(comment)).collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentById(long postId, long commentId) {
        Comment comment = getPostAndComment(postId, commentId);
        return mapToDTO(comment);
    }

    @Override
    public CommentDto updateComment(long postId,long commentId, CommentDto commentDto) {
        Comment comment = getPostAndComment(postId, commentId);
        comment.setName(commentDto.getName());
        comment.setEmail(commentDto.getEmail());
        comment.setBody(commentDto.getBody());
        Comment updatedComment=  commentRepository.save(comment);
        return mapToDTO(updatedComment);
    }

    @Override
    public void deleteComment(long postId, long commentId) {
        Comment comment = getPostAndComment(postId, commentId);
        commentRepository.delete(comment);
    }

    private Comment getPostAndComment(long postId, long commentId) {
        Post post = postRepository.findById(postId).orElseThrow(()-> new ResourceNotFoundException("Post","id",postId));
        Comment comment = commentRepository.findById(commentId).orElseThrow(()->
                new ResourceNotFoundException("Comment","id",commentId));

        if (!comment.getPost().getId().equals(post.getId())){
            throw new BlogApiException(HttpStatus.BAD_REQUEST,"Comment does not belong to post");
        }
        return comment;
    }

    private CommentDto mapToDTO(Comment comment){
        CommentDto commentDto = modelMapper.map(comment,CommentDto.class);
        return commentDto;
    }

    private Comment mapToEntity(CommentDto commentDto){
        Comment comment = modelMapper.map(commentDto,Comment.class);
        return comment;
    }
}
