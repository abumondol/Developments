window_size = 50;
slide = 10;
data = ds2;

fs = [];
for s =1:7
    for h =1 :2
        for sess = 1:5
            for p=1:12
                fprintf('Sub: %d, hand: %d, sess: %d, pose: %d\n', s, h, sess, p);
                d = data(s).hand(h).session(sess).pose(p).data;
                f = calculate_features(d, window_size, slide);
                [r, ~] = size(f);
                z = zeros(r,1);
                fs = [fs; f, z+p, z+h, z+sess, z+s];
            end 
        end
    end
end

csvwrite('fs2.csv',fs);