function model = get_model(data, data_noneat, cluster_centers, params, NumTrees)

    xth = params.xth;
    step_size = params.step_size;
    min_chunk_size_at_xth = params.min_chunk_size_at_xth;
    min_chunk_size = params.min_chunk_size;
    max_chunk_size = params.max_chunk_size;
    
    
    chunks_all = xth_generate_chunks_all(data, xth, step_size, min_chunk_size_at_xth, min_chunk_size, max_chunk_size);    
    chunks_all_noneat = xth_generate_chunks_all(data_noneat, xth, step_size, min_chunk_size_at_xth, min_chunk_size, max_chunk_size);    
    chunk_labels_all = get_chunk_labels(data, chunks_all);
    chunk_data = get_chunk_data(data, data_noneat, chunks_all, chunk_labels_all, chunks_all_noneat); 
    
    %model.chunk_count_before_cluster_filter = length(chunk_data);
    %[chunks, cluster_density] = filter_chunks_by_clusters_train(chunk_data, cluster_centers);
    %model.cluster_density = cluster_density;
    %model.chunk_count_after_cluster_filter = length(chunks);    
    
    count = length(chunk_data);
    X = [];
    Y = zeros(count,1);
    for i = 1:count
        f = get_features_one_window(chunk_data(i).grav);
        X = [X;f];
        Y(i) = chunk_data(i).label;
    end    
    
    %features = features_all_chunks(data, chunks_all);
    %features_noneat = features_all_chunks(data_noneat, chunks_all_noneat);
    %features_combined = features_combine(features, features_noneat, chunk_labels_all); 
    
    Y(Y >= 1000, :) = 0;
    Y(Y < 1000 & Y>0, :) = 1;    
    %rf_model = TreeBagger(NumTrees, X, Y);
    rf_model = fitctree(X, Y);
    model.rf_model = rf_model;
    
end