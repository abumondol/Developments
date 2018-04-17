load('data_usc');
data = data_usc;
offset = 1;

for sub = 3:3    
    d1 = data(sub).grav;
%     x = d1(:, 2:4);
%     mag = sqrt(sum(x.*x, 2));
%     x = x./[mag, mag, mag];
%     d1(:, 2:4) = x;
%     d1(:,2:4) = smooth(d1(:,2:4), 0.90);        
%     
    annots = data(sub).annotations;
    annot_count = size(annots, 1);
    for i = 1:annot_count
        s = annots(i,1);
        label = annots(i,2);            
        close all;        
        
        figure        
        d = d1;
        d = d(d(:,1) >= s-offset & d(:,1) <= s+offset, :);                    

        ttl = strcat('sub:', num2str(sub), ', annot no:', num2str(i), ', time:', time2str(s), ', label:', num2str(label) );            
        x = d(:,2); 
        y = d(:,3); 
        z = d(:,4);        
        plot3(x, y, z );
        xlabel('X'); ylabel('Y'); zlabel('Z');
        xlim([-10, 10]); ylim([-10, 10]); zlim([-10, 10]);
        title(ttl);
        hold on                
        grid on
        d = d(d(:,1) >= s, :);
        x0 = d(1,2);
        y0 = d(1,3);
        z0 = d(1,4);
        scatter3(x0, y0, z0); % annotated mark point

        waitforbuttonpress            
    end
                
end


