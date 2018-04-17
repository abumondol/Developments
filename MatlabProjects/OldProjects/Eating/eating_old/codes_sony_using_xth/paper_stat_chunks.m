
header = [];
for i=1:10       
    params = best_param_res(i).params;    
    xth = params.xth;
    step_size = params.step_size;
    min_chunk_size_at_xth = params.min_chunk_size_at_xth;
    min_chunk_size = params.min_chunk_size;
    max_chunk_size = params.max_chunk_size;
    
    %fprintf('\n*** R: %d, xth:%.2f, step:%.2f, min1:%.2f, min2:%.2f, max: %.2f ***\n', 100-i, xth, step_size, min_chunk_size_at_xth, min_chunk_size, max_chunk_size);
    %chunks_all = xth_generate_chunks_all(data, xth, step_size, min_chunk_size_at_xth, min_chunk_size, max_chunk_size);
    %chunks_all_non_eat = xth_generate_chunks_all(data_non_eat, xth, step_size, min_chunk_size_at_xth, min_chunk_size, max_chunk_size);
    
    chunks_all = params.chunks;   
    chunks_all_non_eat = params.chunks_non_eat;
    d = 0;
    for sub = 1:5
        d = d + length(chunks_all(sub).session(1).chunks) + length(chunks_all(sub).session(2).chunks) + length(chunks_all_non_eat(sub).session(1).chunks);
    end
    
    d
end


