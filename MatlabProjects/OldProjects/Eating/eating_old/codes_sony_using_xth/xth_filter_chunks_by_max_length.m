function [small_chunks, big_chunks] = xth_filter_chunks_by_max_length(chunks, max_length)
    duration = chunks(:,4)-chunks(:,3);
    small_chunks = chunks( duration <= max_length, :);
    big_chunks = chunks( duration > max_length, :);    
end