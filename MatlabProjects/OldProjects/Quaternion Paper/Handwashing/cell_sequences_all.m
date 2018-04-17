ico_number = 4;
cell_sequence = [];


for ds = 1:2
    if ds==1
        data = ds1;        
    else
        data = ds2;        
    end
    
    sub_count = length(data);    
    for sub = 1:sub_count
        for h =1:2
            sess_count = length(data(sub).hand(h).session);
            for sess = 1:sess_count
                pose_count = length(data(sub).hand(h).session(sess).pose);
                for p = 1:pose_count
                    fprintf('dataset: %d, Subject:%d, hand: %d, session:%d, pose:%d\n', ds, sub, h, sess, p);
                    d = data(sub).hand(h).session(sess).pose(p).data;
                    t = 0:0.02:(length(d)-1)*0.02;
                    [cell_assignment, cell_seq] = cell_assignment_and_sequence(t, d(:,13:15), ico, ico_number);
                    cell_sequence(ds).subject(sub).hand(h).session(sess).pose(p).cell_assignment = cell_assignment;
                    cell_sequence(ds).subject(sub).hand(h).session(sess).pose(p).cell_sequence = cell_seq;
                    cell_sequence(ds).subject(sub).hand(h).session(sess).pose(p).summary = cell_sequence_summary(cell_seq);
                end
            end
        end
    end    
end      

save('cell_sequence', 'cell_sequence');
