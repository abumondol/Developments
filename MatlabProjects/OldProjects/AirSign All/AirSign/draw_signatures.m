function draw_signatures(signatures, option)
len = length(signatures);

fprintf('Drawing graphs. Total signatures: %d\n', len);
for i=1:len
    if option == 1
        fprintf('Drawing mark points: %d\n', i);
        draw_sign_marks(signatures(i), 1);
        %draw_sign_marks(signatures(i), 4);
    elseif option == 2
        fprintf('Drawing peak points: %d\n', i);
        draw_sign_peaks(signatures(i), 1);
        draw_sign_peaks(signatures(i), 4);
    elseif option == 3
        draw_for_speed(signatures, 1, 1); 
        return
    end    
end
fprintf('Drawing graphs done\n\n');

end
