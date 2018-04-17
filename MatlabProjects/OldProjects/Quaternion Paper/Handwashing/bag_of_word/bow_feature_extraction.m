window_size = 50;
slide = 10;

for ds = 1:2
    bop = []; %bag of points
    bow = []; %bag of words with count 
    bowbin = []; % bag of words with binary, presence/absence of a cell

    sub_count = length(cell_sequence(ds).subject);
    for sub =1:sub_count        
        for h =1 :2
            sess_count = length(cell_sequence(ds).subject(sub).hand(h).session);
            for sess=1:sess_count
                pose_count = length(cell_sequence(ds).subject(sub).hand(h).session(sess).pose);
                for p=1:pose_count
                    fprintf('Dataset: %d, sub: %d, hand: %d, session: %d, pose: %d\n', ds, sub, h, sess, p);
                    d = cell_sequence(ds).subject(sub).hand(h).session(sess).pose(p).cell_assignment;
                    [bp, bw, bwb] = bow_calculate_features(d, window_size, slide);
                    [r, ~] = size(bp);
                    z = zeros(r,1);
                    z = [z+p, z+h, z+1, z+sub];

                    bop = [bop; bp, z];
                    bow = [bow; bw, z];
                    bowbin = [bowbin; bwb, z];
                end 
            end
        end
    end
    
    if ds ==1
        csvwrite('bop1.csv', bop);
        csvwrite('bow1.csv', bow);
        csvwrite('bowbin1.csv', bowbin);        
    else
        csvwrite('bop2.csv', bop);
        csvwrite('bow2.csv', bow);
        csvwrite('bowbin2.csv', bowbin);        
    end
    
end


