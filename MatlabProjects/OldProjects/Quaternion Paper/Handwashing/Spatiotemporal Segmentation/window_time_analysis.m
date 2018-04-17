window_size = 10;

for ds = 2:2
    durations = [];
    sub_count = length(cell_sequence(ds).subject);
    for sub =1:sub_count        
        for h =1 :2
            sess_count = length(cell_sequence(ds).subject(sub).hand(h).session);
            for sess=1:sess_count
                pose_count = length(cell_sequence(ds).subject(sub).hand(h).session(sess).pose);
                for p=1:pose_count
                    fprintf('Dataset: %d, sub: %d, hand: %d, session: %d, pose: %d\n', ds, sub, h, sess, p);
                    d = cell_sequence(ds).subject(sub).hand(h).session(sess).pose(p).cell_sequence(:, end);
                    count = length(d)-window_size+1;
                    for i=1:count
                        dr = sum(d(i:i+window_size-1, :));
                        durations = [durations; dr];
                        if dr<0.2
                            fprintf('%d, %d, %d, %d, %d, %d: %d\n', ds, sub, h, sess, p, i, dr);
                            waitforbuttonpress
                        end
                    end                    
                end 
            end
        end
    end
   
    %figure;
    %histogram(durations);
    mean(durations)
    [m, I] = min(durations)
    max(durations)
    
    %waitforbuttonpress   
end


