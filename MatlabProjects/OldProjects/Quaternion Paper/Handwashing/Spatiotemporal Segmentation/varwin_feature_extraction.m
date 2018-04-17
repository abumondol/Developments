window_size = 10;

for ds = 1:2
    if ds==1
        data = ds1;
    else
        data = ds2;
    end
    
    fs = [];
    sub_count = length(cell_sequence(ds).subject);
    for sub =1:sub_count        
        for h =1 :2
            sess_count = length(cell_sequence(ds).subject(sub).hand(h).session);
            for sess=1:sess_count
                pose_count = length(cell_sequence(ds).subject(sub).hand(h).session(sess).pose);
                for p=1:pose_count
                    fprintf('Dataset: %d, sub: %d, hand: %d, session: %d, pose: %d\n', ds, sub, h, sess, p);
                    cs = cell_sequence(ds).subject(sub).hand(h).session(sess).pose(p).cell_sequence;
                    d = data(sub).hand(h).session(sess).pose(p).data(:, 4:12);
                    count = length(cs(:,1))-window_size+1;
                    f = zeros(count, 64);
                    for i=1:count
                        s = cs(i, 2);
                        e = cs(i+window_size-1, 3);
                        f(i, 1:63) = calculate_features_one_window(d(s:e, :));
                        f(i, 64) = (e-s+1)*.02;                        
                    end                    
                    
                    z = zeros(count,1);
                    z = [z+p, z+h, z+sess, z+sub];
                    fs = [fs; f, z];                    
                end 
            end
        end
    end
   
   if ds==1
       csvwrite('vfs1.csv', fs);
   else
       csvwrite('vfs2.csv', fs);
   end
end


