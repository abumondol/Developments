%load('data')
offset = 10;

for sub = 3:13    
    d = data(sub).data;
    d(:, 2:4) = mysmooth(d(:, 2:4), 0.8);
    count = length(d);
    
    figure
    i = 1;
    while i<count
        if d(i, end) == 1
            st = i;
            while d(i, end) == 1
                i = i+1;
                continue
            end
            et = i-1;
            
            fprintf('ix: %d, %d, %d', st, et, et-st);
            plot(d(st:et, 2:4));
            legend('X','Y','Z')
            waitforbuttonpress
        end
        
        i = i+1;
   end
                
end


