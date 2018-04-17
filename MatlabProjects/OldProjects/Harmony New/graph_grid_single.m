function graph_grid_single(grav, factor, t, C)
    
    ang=0:0.1:round(2*pi,1);
    ang = ang';
    ap = ones(length(ang),1)*0.99;
    bp=cos(ang)*0.99;
    cp=sin(ang)*0.99;

    figure;
    %plot3([-1;1;1;1], [1;1;-1;-1], [1;1;1;-1]);
    r = 0.9;
    [x,y,z] = sphere(20);
    mesh(x*r, y*r, z*r, 'FaceColor',[0.95 0.95 0.95], 'LineStyle','none'); 
    hold on;
   
%     step = 1/factor;
%     s = -1 + step;
%     e = 1 - step;
%     for x = s:step:e    
%         r = sqrt(1-x*x);    
%         plot3(x*ap, r*bp, r*cp, 'Color','green');
%         plot3(r*bp, x*ap, r*cp, 'Color','blue');
%         plot3(r*bp, r*cp, x*ap, 'Color','red');
%     end
    
    %grav = round(grav*factor)/factor;
    
    plot3(grav(:,1), grav(:,2), grav(:,3),'Color','black');
    scatter3(C(:,1), C(:,2), C(:,3),'filled');
    grid on;
    xlabel('X');
    ylabel('Y');
    zlabel('Z');
    title(t);

end