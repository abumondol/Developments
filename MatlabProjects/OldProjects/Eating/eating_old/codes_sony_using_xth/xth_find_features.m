
header = [];
for i=1:10       
    params = best_param_res(i).params;    
    xth = params.xth;
    step_size = params.step_size;
    min_chunk_size_at_xth = params.min_chunk_size_at_xth;
    min_chunk_size = params.min_chunk_size;
    max_chunk_size = params.max_chunk_size;
    
    fprintf('\n*** R: %d, xth:%.2f, step:%.2f, min1:%.2f, min2:%.2f, max: %.2f ***\n', 100-i, xth, step_size, min_chunk_size_at_xth, min_chunk_size, max_chunk_size);
    %chunks_all = xth_generate_chunks_all(data, xth, step_size, min_chunk_size_at_xth, min_chunk_size, max_chunk_size);
    %chunks_all_non_eat = xth_generate_chunks_all(data_non_eat, xth, step_size, min_chunk_size_at_xth, min_chunk_size, max_chunk_size);
    
    chunks_all = params.chunks;   
    chunks_all_non_eat = params.chunks_non_eat;
    
    f1 = get_features_all_chunks(data, chunks_all);     
    f2 = get_features_all_chunks(data_non_eat, chunks_all_non_eat);
    f = [f1;f2];
    
    col_count = size(f,2);
    if isempty(header)
        header = get_header(col_count);
    end
    dest_path = strcat('features/features_', num2str(i),'.csv');
    fileID = fopen(dest_path,'w');
    fprintf(fileID, header);    
    fclose(fileID);    
    dlmwrite(dest_path, f, '-append');    
end


