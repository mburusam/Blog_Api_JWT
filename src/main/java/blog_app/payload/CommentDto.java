package blog_app.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private long id;
    @NotEmpty(message = "Name should not be Null or Empty")
    private String name;
    @Email
    @NotEmpty(message = "Email should not be null or Empty")
    private String email;
    @NotEmpty
    @Size(min = 10,message = "Comment Body should be minimum 10 characters")
    private String body;

}
