load('segments_pos')
offset = 10;

for sub = 1:16    
    d = segments_pos(sub).segments;
    seg_count = size(d, 1)
    for i = 1:seg_count
        close all
        figure        
        ttl = strcat('sub:', num2str(sub), ', annot no:', num2str(i));                    
        y = reshape(d(i,:), [96, 3]);
        mag = sqrt(sum(y.*y,2));
        v = var(mag)
        plot([y, mag]);        
        title(ttl);        
        grid on
        %plot([0;length(y)], [0; 0], 'k'); %plot zero line                        
        %scatter(0, 0, 'rd'); % annotated mark point        
        waitforbuttonpress

    end
                
end


