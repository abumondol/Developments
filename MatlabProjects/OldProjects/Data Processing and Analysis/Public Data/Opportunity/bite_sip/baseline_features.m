function features = baseline_features(oppdata, newlabels, window_size) 
    features = [];        
    step_size = window_size/2;
    
    for subj = 1:4
        for sess = 1:6
            fprintf('Features: %d, %d\n', subj, sess);
            accel = oppdata{subj,sess}.accel(:, 1:3);
            acn = sqrt(sum(accel.*accel, 2));
            accel = accel./[acn, acn, acn];
            
            labels = newlabels{subj, sess};
            sample_count = length(accel);
            
            for s = 1 : step_size : sample_count - window_size + 1
                e = s + window_size - 1;                
                lb = mode(labels(s:e, 1));
                
                f = features_one_window(accel(s:e, :));                
                features = [features; subj, sess, f, lb];
            end

        end
    end
    
end