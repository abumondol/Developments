sub_count = length(mhealth_data)
features = [];
nan_window_count = 0;
sampling_rate = 50;
window_durations = [1, 2, 3, 6];

for sub = 1:sub_count
    f = [];
    sess_count = length(mhealth_data(sub).session);
    for sess = 1:sess_count
        fprintf('sub:%d, sess:%d\n', sub, sess);
        labels = mhealth_data(sub).session(sess).labels;
        chunks = mhealth_chunks(sub).session(sess).chunks;
        pos_count = length(mhealth_data(sub).session(sess).position);        
        
        for w=1:length(window_durations)
            win_size = window_durations(w)*sampling_rate;            
            for c = 1:length(chunks)
                st = chunks(c, 1);
                ed = chunks(c, 2);
                activity = chunks(c, 3);
                for s =st:win_size/2:ed-win_size+1
                    e = s + win_size - 1;
                    nan_flag = false;
                    
                    temp = [];
                    for pos = 1:pos_count                
                        a = mhealth_data(sub).session(sess).position(pos).accel(s:e, :);                        

                        if sum(sum(isnan(a))) > 0 
                            nan_window_count = nan_window_count + 1;                    
                            nan_flag = true;
                            break;
                        end
                        
                        temp = [temp, features_one_window(a)];                        
                    end                    
                    f = [f; temp, activity, window_durations(w), sess, sub];                
                end
            end                                 
        end
    end
    
    features = [features; f];
    fprintf('%d, %d\n', length(features), nan_window_count);
end

fprintf('%d, %d\n', length(features), nan_window_count);
mhealth_features = features;
save('mhealth_features', 'mhealth_features');
