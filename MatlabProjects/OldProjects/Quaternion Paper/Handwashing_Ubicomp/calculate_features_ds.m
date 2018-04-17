window_size = 50;
slide = 10;
features = [];

for i = 1:2
    sub_count = length(ds(i).subject);
    fs = [];
    for s =1:sub_count
        for h =1 :2
            sess_count = length(ds(i).subject(s).hand(h).session);
            for sess = 1:sess_count
                pose_count = length(ds(i).subject(s).hand(h).session(sess).pose);
                for p=1:pose_count
                    bp = 1;
                    if p == pose_count
                        bp=0;
                    end

                    fprintf('ds:%d, sub: %d, hand: %d, session:%d, pose: %d\n', i, s, h, sess, p);
                    d = ds(i).subject(s).hand(h).session(sess).pose(p).data;
                    ca = cell3assign(i).subject(s).hand(h).session(sess).pose(p).ca;
                    d = [d, ca];

                    f = calculate_features_one_stream(d, window_size, slide);
                    [r, ~] = size(f);
                    z = zeros(r,1);
                    fs = [fs; f, z+sess, z+p, z+bp, z+h, z+s];
                end 
            end
        end
    end
    features(i).features = fs;

end

save('features', 'features');

