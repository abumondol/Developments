function chunks = xth_filter_chunks_by_min_length(chunks, min_length)
    if isempty(chunks)
        return
    end
    duration = chunks(:,4) - chunks(:,3);
    chunks = chunks( duration >= min_length, :);
end