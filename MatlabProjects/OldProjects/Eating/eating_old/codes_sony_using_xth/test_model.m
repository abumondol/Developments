function  [conf_mat, stats] = test_model(data, data_noneat, params, model)

    xth = params.xth;
    step_size = params.step_size;
    min_chunk_size_at_xth = params.min_chunk_size_at_xth;
    min_chunk_size = params.min_chunk_size;
    max_chunk_size = params.max_chunk_size;
    
    
    chunks_all = xth_generate_chunks_all(data, xth, step_size, min_chunk_size_at_xth, min_chunk_size, max_chunk_size);    
    chunks_all_noneat = xth_generate_chunks_all(data_noneat, xth, step_size, min_chunk_size_at_xth, min_chunk_size, max_chunk_size);    
    chunk_labels_all = get_chunk_labels(data, chunks_all);
    chunk_data = get_chunk_data(data, data_noneat, chunks_all, chunk_labels_all, chunks_all_noneat); 
      
    
    count = length(chunk_data);
    X = [];
    Y = zeros(count,1);
    for i = 1:count
        f = get_features_one_window(chunk_data(i).grav);
        X = [X;f];
        Y(i) = chunk_data(i).label;
    end
    
    Y(Y >= 1000, :) = 0;
    Y(Y < 1000 & Y>0, :) = 1;    
    [conf_mat, stats] = classification_test(model, X, Y, [0;1]);
    
end