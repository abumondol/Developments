function res = features_one_session(subj, sess, data, window_size, step_size)    
    
    X = [];
    Y = [];    
    indices = [];
    
    accel = data{subj, sess}.accel;
    g = data{subj, sess}.grav;
    labels = data{subj, sess}.labels(:, 5);
    labels = rename_labels(labels);
    
    sample_count = length(labels);
    for s = 1 : step_size : sample_count - window_size + 1
        e = s + window_size - 1;
        m = round((s+e)/2);
        lb = mode(labels(s:e, 1));

        f = features_one_window(accel(s:e, :));                
        X = [X; f];
        Y = [Y; lb];
        indices = [indices; s, e, m];
    end
    
    res.xy = [g(indices(:,3), :), X, Y];    
    res.indices = indices;
    res.labels = labels;
end