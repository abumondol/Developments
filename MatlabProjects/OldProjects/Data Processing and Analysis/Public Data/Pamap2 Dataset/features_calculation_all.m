sub_count = length(pamap2_data)
features = [];
nan_window_count = 0;
win_size = 100;

for sub = 1:sub_count
    sess_count = length(pamap2_data(sub).session);
    for sess = 1:sess_count
        fprintf('sub:%d, sess:%d\n', sub, sess);
        labels = pamap2_data(sub).session(sess).labels;
        pos_count = length(pamap2_data(sub).session(sess).position);
        count = length(labels);
        
        for s =1:win_size/2:count-win_size
            e = s + win_size - 1;
            act = labels(s+win_size/2-1, :);
            nan_flag = false;
            
            f = [];
            for pos = 1:pos_count                
                a = pamap2_data(sub).session(sess).position(pos).accel(s:e, :);
                g = pamap2_data(sub).session(sess).position(pos).gyro(s:e, :);
                m = pamap2_data(sub).session(sess).position(pos).mag(s:e, :);
                q = pamap2_data(sub).session(sess).position(pos).quat(s:e, :);
                
                if sum(sum(isnan(a))) > 0 || sum(sum(isnan(g))) > 0 || sum(sum(isnan(m))) > 0 
                    nan_window_count = nan_window_count + 1;                    
                    nan_flag = true;
                    break;
                end
                
                [Rx, ~, ~] = quaternion_to_rotation_matrix(q);
                qc = quaternion_conjugate(q(1, :));
                qc = repmat(qc, win_size, 1);
                q = quaternion_multilication(q, qc);
                [~, Ry, Rz] = quaternion_to_rotation_matrix(q);
                
                f = [f, features_one_window(a), features_one_window(g), features_one_window(Rx), features_one_window(Ry), features_one_window(Rz)];                
            end
            
            if nan_flag == false
                features = [features; f, act, sub];            
            end
            
        end
        
    end
    fprintf('%d, %d\n', length(features), nan_window_count);
end

fprintf('%d, %d\n', length(features), nan_window_count);

pamap2_features = features;
save('pamap2_features', 'pamap2_features');
