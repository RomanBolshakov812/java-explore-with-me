package ru.practicum.compilation.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
public class UpdateCompilationRequest {
    private List<Long> events;
    private Boolean pinned;
    @Length(min = 1, max = 50)
    private String title;
}


//public class UpdateCompilationRequest extends NewCompilationDto{
//    public UpdateCompilationRequest(List<Long> events, boolean pinned,
//                                    @Length(min = 1, max = 50) String title) {
//        super(events, pinned, title);
//    }
//}
