window_size = 50;
slide = 10;
data = ds1;

fs = [];
for s =1:9
    for h =1 :2
        for p=1:9
            fprintf('Sub: %d, hand: %d, pose: %d\n', s, h, p);
            d = data(s).hand(h).session(1).pose(p).data;
            f = calculate_features(d, window_size, slide);
            [r, ~] = size(f);
            z = zeros(r,1);
            fs = [fs; f, z+p, z+h, z+1, z+s];
        end 
    end
end

csvwrite('fs1.csv',fs);