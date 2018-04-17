cell3assign = [];
for i = 1:2
    sub_count = length(ds(i).subject);
    for s =1:sub_count
        for h =1 :2
            sess_count = length(ds(i).subject(s).hand(h).session);
            for sess = 1:sess_count
                pose_count = length(ds(i).subject(s).hand(h).session(sess).pose);
                for p=1:pose_count
                    fprintf('ds:%d, sub: %d, hand: %d, session:%d, pose: %d\n', i, s, h, sess, p);
                    d = ds(i).subject(s).hand(h).session(sess).pose(p).data;
                    dcount = length(d);
                    t = 0:0.02:(dcount-1)*0.02;
                    [ca, cs] = spmo_cell_assignment_and_sequence(t, d(:, 13:15), ico, 3);
                    cell3assign(i).subject(s).hand(h).session(sess).pose(p).ca = ca;
                end 
            end
        end
    end
end

save('cell3assign', 'cell3assign');