function chunks = get_chunks(a, t, p)
    
    x_th = p.x_th;
    chunks = filter_chunks_by_threshold(a, x_th);
    chunks = [chunks, t(chunks(:,1), :), t(chunks(:,2), :)];
    chunk_count = size(chunks, 1);

    step_size = 0.25;
    max_chunk_size = 7;
    selected_chunks = [];

    i=1;
    for i = 1:chunk_count    
        res = chunks_from_big_chunk(a, t, chunks(i,:), x_th, step_size, max_chunk_size);
        selected_chunks = [selected_chunks; res];
    end    
    
    chunks = selected_chunks;
end