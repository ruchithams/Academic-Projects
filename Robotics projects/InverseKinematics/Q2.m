% TODO: You write this function!
% input: f -> a 9-joint robot encoded as a SerialLink class
%        qInit -> 1x9 vector denoting current joint configuration
%        posGoal -> 3x1 vector denoting the target position to move to
% output: q -> 1x9 vector of joint angles that cause the end
%                     effector position to reach <position>
%                     (orientation is to be ignored)
function q = Q2(f,qInit,posGoal)
  
  q=qInit;
  for j = 1:200
      
      %fetching joint angles using forward kinematics
      x = f.fkine(q);
      B = transl(x);
       %condition to end loop on reaching goal position
       if B(1) == posGoal(1) && B(2) == posGoal(2) && B(3) == posGoal(3)
           break;
       end
       
      deltaX = (0.05) * (posGoal' - B); 
      
      % calculating jacobian
      J = jacob0(f,q);
      J = J(1 : 3, :);
      deltaQ = pinv(J) * deltaX';
      
      %new joint angles
      q = q + deltaQ';
      
  end
end


