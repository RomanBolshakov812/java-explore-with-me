package ru.practicum.compilation;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

@Transactional
public interface CompilationService {
    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationRequest,
                                     Long compId);

    @Transactional(readOnly = true)
    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    @Transactional(readOnly = true)
    CompilationDto getCompilationById(Long compId);
}
