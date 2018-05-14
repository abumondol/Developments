function chunks_selected = xth_generate_chunks_one_session(accel, xth, step_size, min_chunk_size_at_xth, min_chunk_size, max_chunk_size)

    chunks = xth_filter_chunks_by_threshold(accel, xth); % discard all data above threshold                     
    [chunks, big_chunks] = xth_filter_chunks_by_max_length(chunks, max_chunk_size);                         
    chunks = xth_expand_chunks(accel, chunks); % expands each chunk to both side to get the eating window
    chunks = xth_filter_chunks_by_min_length(chunks, min_chunk_size_at_xth); % filter out smaller hunks       

    c1 = xth_divide_big_chunks(accel, big_chunks, xth, step_size, max_chunk_size);
    c1 = xth_expand_chunks(accel, c1); % expands each chunk to both side to get the eating window
    c1 = xth_filter_chunks_by_min_length(c1, min_chunk_size); % filter out smaller chunks 

    chunks = [chunks, zeros(size(chunks,1), 1) + xth];
    chunks = [chunks;c1];        
    chunks = sortrows(chunks);
    
    durations = chunks(:,4) - chunks(:,3);
    chunks = [chunks, durations];
    
    chunks_selected = [];
    chunk_count = size(chunks,1);
    for i = 1:chunk_count
        s = chunks(i,1);
        e = chunks(i,2);
        x = accel(s:e, 2); 
        [min_val, min_ind] = min(x);    
        len = e-s+1;        
        if min_ind > 3 && len - min_ind + 1 > 3
            chunks_selected =  [chunks_selected; chunks(i, :)]; 
%         else
%             fprintf('problem: %d, %d, %d, %d, %d\n', s, e, len, min_ind, min_val);
%             d = accel(s:e, 2:4);
%             figure;
%             plot(d);
%             legend('x','y','z');
%             waitforbuttonpress
        end
    end
end


